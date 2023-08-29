# 导入标准库及refprop模块
import os
import numpy as np
import datetime
import pandas as pd
import sys
from time import sleep
import os
from RLI_calc import get_data_from_db as gdfd
# import get_data_from_db as gdfd

from ctREFPROP.ctREFPROP import REFPROPFunctionLibrary

# 调用动态链接与初始化制冷剂参数
RP = REFPROPFunctionLibrary(
    'C:\\Program Files (x86)\\REFPROP\\REFPRP64.DLL', 'dll')
# RP.SETPATHdll(os.environ['RPPREFIX'])
r = RP.SETMIXdll('R410A.mix', "HMX.BNC", "DEF")
# print('molecular weight:',RP.WMOLdll(r.z), 'g/mol')
basepath = os.path.abspath('')


def dataWrite(df, targetFile):
    # if os.path.exists(targetFile) == True:
    #     df.to_csv(targetFile,sep = ',',encoding='utf-8',header=False,index=False,mode="a")
    # elif os.path.exists(targetFile) == False:
    #     df.to_csv(targetFile,sep = ',',encoding='utf-8',header=True,index=False,mode="a")
    df.to_csv(targetFile, sep=',', encoding='utf-8', header=True, index=False)

# 根据温度调用饱和状态参数


def satTCalSatFluidP(tempture):  # T:单位K，P单位：kPa
    sat = RP.SATTdll(tempture, r.z, 1)
    return sat.P


def satTCalSatGasP(tempture):  # T:单位K，P单位：kPa
    sat = RP.SATTdll(tempture, r.z, 2)
    return sat.P

# 根据压力调用饱和状态参数


def satPCalSatFluidT(pressure):  # T:单位K，P单位：kPa
    sat = RP.SATPdll(pressure, r.z, 1)
    return sat.T


def satPCalSatGasT(pressure):  # T:单位K，P单位：kPa
    sat = RP.SATPdll(pressure, r.z, 2)
    return sat.T

# 根据压力调用饱和状态参数


def satPCalSatFluidS(pressure):
    sat = RP.PQFLSHdll(pressure, 0, r.z, 1)
    return sat.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)


def satPCalSatGasS(pressure):
    sat = RP.PQFLSHdll(pressure, 1, r.z, 1)
    return sat.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)

# 根据温度和压力来调用


def targetTPCalS(tempture, pressure):
    fla = RP.TPFLSHdll(tempture, pressure, r.z)
    return fla.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)


def RLICalSave_cool(dfRLI, saveFile):
    # columnsChoose = ['UNIT_ID', 'Datetime', 'OU1Pc', 'OU1Pe','OU1Thexdi']
    # dfRLI = df[columnsChoose].copy()
    # 单位换算
    dfRLI['OU1PcMKS'] = dfRLI['OU1Pc'].apply(
        lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa
    dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'].apply(
        lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa

    # # 家中经过换算已经为绝对压力
    # dfRLI['OU1PcMKS'] = dfRLI['OU1Pc']  # 绝对压力 kPa
    # dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'] # 绝对压力 kPa

    dfRLI['OU1ThexdiMKS'] = dfRLI['OU1ThexLiq'].apply(
        lambda x: x+273.15)  # ℃ → k

    dfRLI['Tc(Rp)'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)
    dfRLI['Te(Rp)'] = dfRLI['OU1PeMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)

    dfRLI['TCondTPIn'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatGasT(x) - 273.15)
    dfRLI['TCondTPOut'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)

    dfRLI['SCondTPin'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatGasS(x))
    dfRLI['SCondTPOut'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatFluidS(x))

    dfRLI['TCondDi'] = dfRLI['OU1ThexLiq']
    dfRLI['SConddi'] = dfRLI.apply(lambda x: targetTPCalS(
        x['OU1ThexdiMKS'], x['OU1PcMKS']), axis=1)

    dfRLI['ACondSC_Te'] = dfRLI.apply(lambda x: ((x['TCondTPOut']-x['Te(Rp)'])+(
        x['TCondDi']-x['Te(Rp)']))*(x['SCondTPOut']-x['SConddi'])/2, axis=1)
    dfRLI['ACondTP_Te'] = dfRLI.apply(lambda x: ((x['TCondTPIn']-x['Te(Rp)'])+(
        x['TCondTPOut']-x['Te(Rp)']))*(x['SCondTPin']-x['SCondTPOut'])/2, axis=1)
    dfRLI['RLI(Te)'] = dfRLI.apply(lambda x: (x['ACondSC_Te'] /
                                              x['ACondTP_Te']) if x['ACondTP_Te'] != 0 else 0, axis=1)
    dfRLI['RLI(Te)'] = dfRLI['RLI(Te)'].apply(
        lambda x: round(x, 6) if x > 0 else 0)

    dfRLI['SHdis'] = dfRLI.apply(lambda x: round(
        (x['OU1Td'] - x['Tc(Rp)']), 6), axis=1)
    dfRLI['SHdis'] = dfRLI['SHdis'].apply(lambda x: 0 if x < 0 else x)

    dfRLI['SHsuc'] = dfRLI.apply(lambda x: round(
        (x['OU1TAccIn'] - x['Te(Rp)']), 6), axis=1)
    dfRLI['SHsuc'] = dfRLI['SHsuc'].apply(lambda x: 0 if x < 0 else x)

    dfRLI['SCconddi(Rp)'] = dfRLI['TCondTPOut'] - dfRLI['TCondDi']
    dfRLI['SCconddi(Rp)'] = dfRLI['SCconddi(Rp)'].apply(lambda x: round(x, 6))
    dfRLI['ModifySCDi(Rp)'] = dfRLI.apply(lambda x: x['SCconddi(Rp)'] /
                                          (x['Tc(Rp)']-x['OU1Ta']) if (x['Tc(Rp)']-x['OU1Ta']) > 0 else 0, axis=1)

    dfRLI['CCDI'] = np.nan
    dfRLI['RLILiq'] = dfRLI['RLI(Te)']

    # return dfRLI
    # # 数据保存
    dataWrite(dfRLI, saveFile)


def RLICalSave_heat(dfRLI, saveFile):
    # columnsChoose = ['UNIT_ID', 'Datetime', 'OU1Pc', 'OU1Pe','OU1Thexdi']
    # dfRLI = df[columnsChoose].copy()
    # 单位换算
    dfRLI['OU1PcMKS'] = dfRLI['OU1Pc'].apply(
        lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa
    dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'].apply(
        lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa

    # # 家中经过换算已经为绝对压力
    # dfRLI['OU1PcMKS'] = dfRLI['OU1Pc']  # 绝对压力 kPa
    # dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'] # 绝对压力 kPa

    # 计算Tc(Rp),Te(Rp)
    dfRLI['Tc(Rp)'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)
    dfRLI['Te(Rp)'] = dfRLI['OU1PeMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)

    # 计算SHdis
    dfRLI['SHdis'] = dfRLI.apply(lambda x: round(
        (x['OU1Td'] - x['Tc(Rp)']), 6), axis=1)
    dfRLI['SHdis'] = dfRLI['SHdis'].apply(lambda x: 0 if x < 0 else x)

    # 计算SHsuc
    dfRLI['SHsuc'] = dfRLI.apply(lambda x: round(
        (x['OU1TAccIn'] - x['Te(Rp)']), 6), axis=1)
    dfRLI['SHsuc'] = dfRLI['SHsuc'].apply(lambda x: 0 if x < 0 else x)

    # 计算SCLiq(Rp)
    dfRLI['TLiq'] = dfRLI['OU1TLiqPipe']
    dfRLI['TCondTPOut'] = dfRLI['OU1PcMKS'].apply(
        lambda x: satPCalSatFluidT(x) - 273.15)
    dfRLI['TLiq'] = dfRLI.apply(
        lambda x: x['TCondTPOut']-0.001 if x['TLiq'] > x['TCondTPOut'] else x['TLiq'], axis=1)
    dfRLI['SCLiq(Rp)'] = dfRLI.apply(
        lambda x: round(x['TCondTPOut']-x['TLiq'], 6), axis=1)

    # 计算ModifySC(Rp)
    dfRLI['ModifySC(Rp)'] = dfRLI.apply(lambda x: x['SCLiq(Rp)'] /
                                        (x['Tc(Rp)']-x['OU1Ta']) if x['Tc(Rp)'] > x['OU1Ta'] else 0, axis=1)

    # 计算RLI(Rp)
    dfRLI['RLI(Rp)'] = dfRLI.apply(lambda x: round((x['TLiq']-x['Te(Rp)'])/(x['Tc(Rp)']-x['Te(Rp)']), 7)
                                   if (x['Tc(Rp)'] > x['Te(Rp)']) & (x['TLiq'] > x['Te(Rp)']) else 0, axis=1)

    # CCDI
    dfRLI['CCDI'] = np.nan
    # # 数据保存
    dataWrite(dfRLI, saveFile)

# argv[1] : 机种代码 如405,406,407,446
# argv[2] : 计算后数据保存地址


def main(argv):
    # 获取现在时刻
    nowtime = datetime.datetime.now()
    # print('现在时刻: ',nowtime)
    # #抽取最晚时刻（减去一天）
    extracttime = (nowtime + datetime.timedelta(days=-1)).strftime(format='%Y-%m-%d %H:%M:%S')
    # print('数据抽取最晚时刻: ',extracttime)

    monthpath = str(nowtime.month)+'month'
    fileSaveAll = os.path.join(
        basepath, 'RLICalculationResult', argv, monthpath, str(nowtime.day))
    if os.path.exists(fileSaveAll) == False:
        os.makedirs(fileSaveAll)

    # 读取抽取数据定义表
    excelpath = os.path.join(basepath, 'COLUMNS_LIST.xlsx')
    info = pd.read_excel(excelpath)
    if 'IVRV字段_{}'.format(argv) not in info.columns:
        print('TYPE_ID  not extist!')
        sys.exit()
    info = info[['共通项目名_Python', '表', 'IVRV字段_{}'.format(argv)]]
    info.dropna(inplace=True)
    info.columns = ['python_col', 'table', 'ivrv_col']

    # 需要抽取的rt表的内容
    rt_info = info[info['table'] == 'RT']
    rt_ivrv_cols = ','.join(rt_info['ivrv_col'].tolist())
    rt_python_cols = rt_info['python_col'].tolist()

    # 需要抽取的tl表的内容
    tl_info = info[info['table'] == 'TL']
    if len(tl_info) == 0:
        tl_ivrv_cols = ''
    else:
        tl_ivrv_cols = ','.join(
            ['REPORT_DT_TZ'] + tl_info['ivrv_col'].tolist())
        tl_python_cols = tl_info['python_col'].tolist()

    out_unit_id_db = gdfd.get_unit_from_DB(argv)
    out_unit_id = out_unit_id_db.values.tolist()
    # tl_data在单台的情况下不需要用到(部分)
    # tl_ivrv_cols = ''
    # 需要抽取的tl表的内容
    # gdfd.get_data_from_DB()
    # 分别获取各个out_unit_id的数据
    for UNIT_ID in out_unit_id:
        try:
            rt_data, tl_data = gdfd.get_data_from_DB(type_id=argv,
                                                     unit_id=UNIT_ID[0],
                                                     extracttime=extracttime,
                                                     rt_cols=rt_ivrv_cols,
                                                     tl_cols=tl_ivrv_cols)
            rt_data.columns = rt_python_cols

            if isinstance(tl_data, str):
                pass
            else:
                tl_data.columns = ['Datetime'] + tl_python_cols

            data = pd.merge(rt_data, tl_data, on='Datetime', how='inner')

            data_temp = data[data['Ctrlmode'] == 4]
            if len(data_temp) > 0:
                saveFilePath = os.path.join(fileSaveAll, 'cool')
                if os.path.exists(saveFilePath) == False:
                    os.makedirs(saveFilePath)
                saveFile = f"{saveFilePath}\{str(UNIT_ID[0])+'.csv'}"
                # 计算并保存
                RLICalSave_cool(data_temp.copy(), saveFile)

            data_temp = data[data['Ctrlmode'] == 19]
            if len(data_temp) > 0:
                saveFilePath = os.path.join(fileSaveAll, 'heat')
                if os.path.exists(saveFilePath) == False:
                    os.makedirs(saveFilePath)
                saveFile = f"{saveFilePath}\{str(UNIT_ID[0])+'.csv'}"
                # 计算并保存
                RLICalSave_heat(data_temp.copy(), saveFile)

        except Exception as e:
            print(e)
            continue
    sleep(1)
    return fileSaveAll


if __name__ == '__main__':
    main(sys.argv)
