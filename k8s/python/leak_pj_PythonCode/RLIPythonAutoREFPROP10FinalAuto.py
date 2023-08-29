#导入标准库及refprop模块 
from decimal import  * 
import os
from time import sleep
import numpy as np 
from ctREFPROP.ctREFPROP import REFPROPFunctionLibrary
from leakdetectionfunc import operation_mysql

# 调用动态链接与初始化制冷剂参数
# RP = REFPROPFunctionLibrary('C:\\Program Files (x86)\\REFPROP\\REFPRP64.DLL', 'dll') 
RP = REFPROPFunctionLibrary('C:\\Program Files (x86)\\REFPROP\\REFPRP64.DLL', 'dll') 
# RP.SETPATHdll(os.environ['RPPREFIX'])
r=RP.SETMIXdll('R410A.mix',"HMX.BNC","DEF")
# print('molecular weight:',RP.WMOLdll(r.z), 'g/mol')
basepath = os.path.abspath('')

def dataWrite(df,targetFile):
    df.to_csv(targetFile,sep = ',',encoding='utf-8',header=True,index=False)

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
    sat=RP.PQFLSHdll(pressure,0,r.z,1)
    return sat.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)
def satPCalSatGasS(pressure):
    sat=RP.PQFLSHdll(pressure,1,r.z,1)
    return sat.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)

# 根据温度和压力来调用 
def targetTPCalS(tempture,pressure):
    fla = RP.TPFLSHdll(tempture,pressure, r.z) 
    return fla.s / RP.WMOLdll(r.z)  # 单位：kJ/(kg·K)



# def RLICalSave(dfRLI,saveFile):
def RLICalSave(dfRLI,saveFile):
    # columnsChoose = ['UNIT_ID', 'Datetime', 'OU1Pc', 'OU1Pe','OU1Thexdi']
    # dfRLI = df[columnsChoose].copy()
    # 单位换算
    dfRLI['OU1PcMKS'] = dfRLI['OU1Pc'].apply(lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa
    dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'].apply(lambda x: (x+1.03323)*98.0665)  # kgf/cm2 表压 → 绝对压力 kPa

    # # 家中经过换算已经为绝对压力
    # dfRLI['OU1PcMKS'] = dfRLI['OU1Pc']  # 绝对压力 kPa
    # dfRLI['OU1PeMKS'] = dfRLI['OU1Pe'] # 绝对压力 kPa


    dfRLI['OU1ThexdiMKS'] = dfRLI['OU1Thexdi'].apply(lambda x: x+273.15)  # ℃ → k

    dfRLI['Tc(Rp)'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatFluidT(x) - 273.15)
    dfRLI['Te(Rp)'] = dfRLI['OU1PeMKS'].apply(lambda x: satPCalSatFluidT(x) - 273.15)

    dfRLI['TCondTPIn'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatGasT(x) - 273.15)
    dfRLI['TCondTPOut'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatFluidT(x) - 273.15)

    dfRLI['SCondTPin'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatGasS(x))
    dfRLI['SCondTPOut'] = dfRLI['OU1PcMKS'].apply(lambda x: satPCalSatFluidS(x))

    dfRLI['TCondDi'] = dfRLI['OU1Thexdi']
    dfRLI['SConddi'] = dfRLI.apply(lambda x: targetTPCalS(x['OU1ThexdiMKS'],x['OU1PcMKS']),axis=1)

    dfRLI['ACondSC_Te'] = dfRLI.apply(lambda x:((x['TCondTPOut']-x['Te(Rp)'])+(x['TCondDi']-x['Te(Rp)']))*(x['SCondTPOut']-x['SConddi'])/2,axis=1)
    dfRLI['ACondTP_Te'] = dfRLI.apply(lambda x:((x['TCondTPIn']-x['Te(Rp)'])+(x['TCondTPOut']-x['Te(Rp)']))*(x['SCondTPin']-x['SCondTPOut'])/2,axis=1)
    dfRLI['RLI(Te)'] = dfRLI.apply(lambda x: (x['ACondSC_Te'] / x['ACondTP_Te']) if x['ACondTP_Te'] != 0 else 0,axis=1)
    dfRLI['RLI(Te)'] = dfRLI['RLI(Te)'].apply(lambda x: round(x,6) if x>0 else 0)

    dfRLI['SHdis'] = dfRLI.apply(lambda x: round((x['OU1Td'] - x['Tc(Rp)']),6),axis=1)
    dfRLI['SHdis'] = dfRLI['SHdis'].apply(lambda x: 0 if x<0 else x)

    dfRLI['SHsuc'] = dfRLI.apply(lambda x: round((x['OU1TAccIn'] - x['Te(Rp)']),6),axis=1)
    dfRLI['SHsuc'] = dfRLI['SHsuc'].apply(lambda x: 0 if x<0 else x)

    dfRLI['SCconddi(Rp)'] = dfRLI['TCondTPOut'] - dfRLI['TCondDi']
    dfRLI['SCconddi(Rp)'] = dfRLI['SCconddi(Rp)'].apply(lambda x: round(x,6))
    dfRLI['ModifySCDi(Rp)'] = dfRLI.apply(lambda x: x['SCconddi(Rp)']/(x['Tc(Rp)']-x['OU1Ta']) if (x['Tc(Rp)']-x['OU1Ta'])>0 else 0,axis = 1) 

    dfRLI['CCDI'] = np.nan
    dfRLI['RLILiq'] = dfRLI['RLI(Te)']

    # return dfRLI
    # # 数据保存
    dataWrite(dfRLI,saveFile)
    

def main(modelcode):
    import datetime
    
    # 开始时间
    startTime = datetime.datetime.now()

    import pandas as pd
    monthpath = str(startTime.month)+'month'
    fileSaveAll = os.path.join(basepath,'RLICalculationResult',modelcode,monthpath)
    if os.path.exists(fileSaveAll) == False:
        os.makedirs(fileSaveAll)

    # 数据调取
    # csvDatafile = r'D:\PYTHON\TIC_CCDI_压缩机故障预兆检知\awsIvrvDataDownload\awsIvrvDataDownload447'
    # csvDatafile = os.path.join(basepath,'RLI待计算数据')
    # if os.path.exists(csvDatafile) == False:
    #     os.makedirs(csvDatafile)

    VRVColumnsList = ["UNIT_ID","REPORT_DT_TZ","R1T1","R4T1","R5T1","R6T1","R7T1","TC1","TE1","TCG1","TEG1",
        "Y1E1","Y2E1","BHP1","FSTEP1","CTMODE","INYO","R9T1","CINV11","RINV11","R21T1","RFAN11",
        "RFAN21","HTOTEMP1","INVPSUM11","INVSTOP11","DEFROST"]


    moduleColumnsList = ["UNIT_ID","Datetime","OU1Ta","OU1ThexLiq","OU1Thexdi","OU1Tsh","OU1TLiqPipe","OU1Pc","OU1Pe","OU1Tc","OU1Te",
    "OU1EV1pls","OU1EV2pls","OU1HP","OU1FanStep","Ctrlmode","IU_TON_Cap","OU1TAccIn","OU1INVCur","OU1INVrps","OU1Td","OU1Fanrps",
    "OU1Fan2rps","OU1TdMax","OU1INVCopt","OU1INVCOnOffN","DefrostN"]

    sql = "SELECT DISTINCT UNIT_ID FROM repunit_rt_"+modelcode   
    result=operation_mysql.selectSql(sql)
 
    for lis in result:
        sql2 = "SELECT * FROM repunit_rt_"+modelcode+" WHERE UNIT_ID = '"+str(lis['UNIT_ID'])+"' ORDER BY REPORT_DT DESC LIMIT 1000 "
        result2 = operation_mysql.selectSql(sql2)
        df=pd.DataFrame(result2)

        df.rename(columns=dict(zip(VRVColumnsList,moduleColumnsList)),inplace=True)
        saveFile = f"{fileSaveAll}\{str(lis['UNIT_ID'])+'.csv'}"

        RLICalSave(df,saveFile)
    # 结束时间
    endTime = datetime.datetime.now()
    print('时间测试',endTime - startTime)
    sleep(3)
    return fileSaveAll

if __name__ == '__main__':
    main()
