
from concurrent.futures import ThreadPoolExecutor
import encodings
import json
import shutil
from time import sleep
import time
import csv
import pandas as pd
import tkinter, tkinter.filedialog
import os 
import json
import numpy as np
import ast
from os import path
import glob
import datetime
import matplotlib.dates as mdates
import matplotlib.pyplot as plt
import math
import matplotlib.colors as clr
#モデル用
# from sklearn.preprocessing import StandardScaler
from sqlalchemy import create_engine
#正常時RLIモデルに応じて、変える部分
from leakdetectionfunc import CalcdeltaRLIOutputExcelCoolHeatHP as clcdeltaRLI
from leakdetectionfunc import leak_judgement as leakjudge
from leakdetectionfunc import wet_and_TbMF_judgement as wtjudge
from leakdetectionfunc import visualization as visual
from leakdetectionfunc import mk_matrics as mk_matrics
from leakdetectionfunc import operation_mysql
from RLI_calc import RLI_clac_multi
from RLI_calc import RLI_clac_single
#混同行列
import seaborn as sns
color = sns.color_palette()
from sklearn.metrics import confusion_matrix
from sklearn.metrics import accuracy_score
from multiprocessing import Pool
import logging
import sys
dt_now = datetime.datetime.now()
create_time=time.strftime('%Y/%m/%d',time.localtime(time.time()))

def leakage_calc(x):  #计算泄漏量用 2022/10/26追加
    if x>=0:
        return 0
    else:
        v = round((5.9929*(-x)**3-28.661*(-x)**2+8.1588*(-x)-0.0004)*100,2)
        v=0 if v<0 else v
        return v

def dummyfunc(df,modelcode,opeMode,horsepower,w_size,n_JudgeDay,label_thr,proba_thr,resultpath,lc_number,calcmode):
    refType = 'R410A.MIX'
    file_name = 'testfile'

    #ΔRLIを計算する
    #データフレームの型を次で使えるように変換。
    for w in df.columns.values:
        df[w] = pd.to_numeric(df[w], errors='ignore')

    df_dRLI = clcdeltaRLI.main(df,modelcode,horsepower,opeMode,calcmode)
    df_dRLI = df_dRLI.reset_index()
    print(' -> Done.\n') 

    #冷媒漏洩してるかどうか判定する
    print('judging normal or abnormal...')
    outputter = leakjudge.leak_detection(opeMode,w_size,n_JudgeDay,label_thr,0.08,100,proba_thr,calcmode) #数字小さいと異常寄り
    df_leak, df_lag, df_dRLI_after, detect_label, pre_span = outputter.run(df_dRLI)

    # if pre_span=='':  #修改于220704 利用
    if detect_label == 0: 
        detect_label = 'normal'
    else: 
        # #湿りかどうか判定する -> 湿り判定を削除し、全てLeakラベルを付与
        detect_label = 'leak'     
        # print(datetime.datetime.now())
        print('judge leak, wet or Tb error...')  
        wnt = wtjudge.classify(opeMode,modelcode)
        detect_label, pre_span = wnt.run(df_lag["label_hour"], df_dRLI_after)
        print(' ---> '+detect_label+'\n')

    #結果を出力する non Requested process, 
    # print('Process of visualization...\n')
    # if type(df_dRLI_after) != str:
    #     visual.visualize(resultpath, detect_label,pre_span,df_dRLI_after,opeMode,file_name,lc_number)
    # print(' -> Done.\n')
    
    return df_leak, df_lag, df_dRLI_after, detect_label, pre_span   


def Check_RLI_clac_single(modelcode,calcmode):
    testpath = RLI_clac_single.main(modelcode)
    w_size = 60 if calcmode==1 else 10
    n_JudgeDay=1
    label_thr_list=[0.6]
    proba_thr_list=[0.46]

    allpath = glob.glob(testpath + r'/*')
    for i, path in enumerate(allpath):
        pathname=os.path.basename(path)
        if pathname=='cool':
            opemode='1'
            coolheat = 'hp_cool'
        if pathname=='heat':
            opemode='2'
            coolheat = 'hp_heat'

        print('Loading...\n')
        
        save_dir='Result_'+modelcode
        basepath = os.path.abspath('')
        allcsvpath = glob.glob(path + r'/*.csv')
        timename=str(dt_now.month)+"-"+str(dt_now.day)+'_win'+str(w_size)
           
        # 判定　閾値を総当たり
        for label_thr in label_thr_list: 
            for proba_thr in proba_thr_list:
                save_dir='Result_'+modelcode + '_P_{}_L_{}'.format(proba_thr,label_thr)
                resultpath = basepath + r'/Result/'+coolheat+'/'+save_dir+'/'+str(timename)
                os.makedirs(resultpath+'/Judgement', exist_ok=True)
                os.makedirs(resultpath+'/Matrics', exist_ok=True)
                os.makedirs(resultpath+'/PNGdata', exist_ok=True)
                
                if os.path.exists(resultpath+'/DetailResult'):
                    shutil.rmtree(resultpath+'/DetailResult')
                    os.makedirs(resultpath+'/DetailResult',exist_ok=True) #2022/09/02追加，用于保存各时刻的判定状态
                else:
                    os.makedirs(resultpath+'/DetailResult',exist_ok=True) #2022/09/02追加，用于保存各时刻的判定状态
                result_columns = ['month','dRLI_t','dRLI_t+1','RLI','Ta','DSH','label','SH','EV2','leak_prob','anomality','label_hour','mold','opemode','leakage','UNIT_ID']
                result_df = pd.DataFrame(columns=result_columns)
                result_df.to_csv(resultpath+'/DetailResult/result.csv',encoding='utf-8-sig')

                result_columnJudge = ['UNIT_ID','detect_Label','opemode','leakage','leak_Dt','creat_Dt']
                result_Judge = pd.DataFrame(columns=result_columnJudge)
                result_Judge.to_csv(resultpath+'/Judgement/Judge.csv',encoding='utf-8-sig')

                list_label = []
                 
                for i, csvpath in enumerate(allcsvpath):
                    try:   
                        leakage_value =0
                        check_result='normal'
                        list_Judge = []
                        print(csvpath)
                        df = pd.read_csv(csvpath,encoding='utf-8-sig')      # read VRV operation data from xlsx
                        if calcmode == 1:     
                            if len(df)<100:
                                print('ERROR: Insufficient dara')
                                continue
                        else:
                            if len(df)<30:
                                print('ERROR: Insufficient dara')
                                continue
                        hp = df.loc[0,'OU1HP']
                        lc_number=str(df.loc[0,'UNIT_ID'])
                        df_leak, df_lag, df_dRLI_after, detect_label, pre_span = dummyfunc(df,modelcode,opemode, hp,w_size,n_JudgeDay,label_thr,proba_thr,resultpath,str(lc_number),calcmode)
                        
                        df_lag['mold'] = 1
                        df_lag['opemode'] = opemode
                        df_lag['leakage'] = df_lag['dRLI_t'].map(lambda x:leakage_calc(x))
                        df_lag['UNIT_ID'] = lc_number
                        df_lag['datetime'] = df_lag.index
                        df_lag.to_csv(resultpath+'/DetailResult/result.csv',mode='a',header=False,encoding='utf-8-sig')

                        df_lag_list = np.array(df_lag).tolist()
                        if len(df_lag_list)>0:
                            sql_1 = 'insert ignore into detailresult(month,dRLI_t,dRLI_tt,RLI,Ta,DSH,label,SH,EV2,leak_prob,anomality,label_hour,mold,opemode,leakage,UNIT_ID,CREATE_DT) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)' 
                            operation_mysql.insertSql(sql_1,df_lag_list)
                        if len(detect_label)>0:
                            if detect_label=='leak':
                                temp_value = round(df_lag[(df_lag['label_hour']==1)&(df_lag['label']==1)]['leakage'].mean(),2)
                                leakage_value=temp_value if not math.isnan(temp_value) else 0
                                if leakage_value<25:
                                    leakage_value=25
                                check_result=detect_label
                            list_Judge=[lc_number, check_result,opemode,leakage_value,pre_span,create_time]
                        if len(lc_number)>0:
                            list_label.append(list_Judge) 
                    except Exception as e:
                        print(e)
                        pass
                if len(list_label)>0:
                    sql_2 = 'insert ignore into judgementresult(UNIT_ID,detect_Label,opemode,leakage,leak_Dt,creat_Dt) values(%s,%s,%s,%s,%s,%s)' 
                    operation_mysql.insertSql(sql_2,list_label) 
                    result_Judge = pd.DataFrame(data=list_label)
                    result_Judge.to_csv(resultpath+'/Judgement/Judge.csv',mode='a',header=False,encoding='utf-8-sig')
            
            

def Check_RLI_clac_multi(modelcode,calcmode):
    testpath = RLI_clac_multi.main(modelcode)
    w_size = 60 if calcmode==1 else 10
    n_JudgeDay=1
    label_thr_list=[0.6]
    proba_thr_list=[0.46]
    allpath = glob.glob(testpath + r'/*')

    for i, path in enumerate(allpath):
        pathname=os.path.basename(path)
        if pathname=='cool':
            opemode='1'
            coolheat = 'hp_cool'
        if pathname=='heat':
            opemode='2'
            coolheat = 'hp_heat'   
        save_dir='Result_'+modelcode
        basepath = os.path.abspath('')
        
        allpath = glob.glob(path + r'/*')
        timename=str(dt_now.month)+"-"+str(dt_now.day)+'_win'+str(w_size)

        # 判定　閾値を総当たり
        for label_thr in label_thr_list:
            for proba_thr in proba_thr_list:
                save_dir='Result_'+modelcode + '_P_{}_L_{}'.format(proba_thr,label_thr)
                resultpath = basepath + r'/Result/'+coolheat+'/'+save_dir+'/'+str(timename)
                os.makedirs(resultpath+'/Judgement', exist_ok=True)
                os.makedirs(resultpath+'/Matrics', exist_ok=True)
                os.makedirs(resultpath+'/PNGdata', exist_ok=True)
                
                if os.path.exists(resultpath+'/DetailResult'):
                    shutil.rmtree(resultpath+'/DetailResult')
                    os.makedirs(resultpath+'/DetailResult',exist_ok=True) #2022/09/02追加，用于保存各时刻的判定状态
                else:
                    os.makedirs(resultpath+'/DetailResult',exist_ok=True) #2022/09/02追加，用于保存各时刻的判定状态
                result_columns = ['month','dRLI_t','dRLI_t+1','RLI','Ta','DSH','label','SH','EV2','leak_prob','anomality','label_hour','mold','opemode','leakage','UNIT_ID']
                result_df = pd.DataFrame(columns=result_columns)
                result_df.to_csv(resultpath+'/DetailResult/result.csv',encoding='utf-8-sig')


                result_columnJudge = ['UNIT_ID','detect_Label','opemode','leakage','leak_Dt','creat_Dt']
                result_Judge = pd.DataFrame(columns=result_columnJudge)
                result_Judge.to_csv(resultpath+'/Judgement/Judge.csv',encoding='utf-8-sig')

                list_label = []
            
                for i, path in enumerate(allpath):
                    try:   
                        allcsvpath = glob.glob(path + r'/*.csv')
                        check_result='normal'
                        leakage_value=0
                        lc_number=''
                        list_Judge=[lc_number, check_result,opemode,leakage_value,'',create_time]
                        for i, csvpath in enumerate(allcsvpath): 
                            csvmode=os.path.basename(csvpath).split('.')[0]
                            print(csvpath)
                            df = pd.read_csv(csvpath,encoding='utf-8-sig')     
                            if calcmode == 1:     
                                if len(df)<100:
                                    print('ERROR: Insufficient dara')
                                    continue
                            else:
                                if len(df)<30:
                                    print('ERROR: Insufficient dara')
                                    continue 
                            hp = df.loc[0,'OU1HP']
                            lc_number=str(df.loc[0,'UNIT_ID'])
                            df_leak, df_lag, df_dRLI_after, detect_label, pre_span = dummyfunc(df,modelcode,opemode, hp,w_size,n_JudgeDay,label_thr,proba_thr,resultpath,str(lc_number),calcmode)
                             
                            df_lag['mold'] = csvmode
                            df_lag['opemode'] = opemode
                            df_lag['leakage'] = df_lag['dRLI_t'].map(lambda x:leakage_calc(x))
                            df_lag['UNIT_ID'] = lc_number
                            df_lag['datetime'] = df_lag.index
            
                            df_lag.to_csv(resultpath+'/DetailResult/result.csv',mode='a',header=False,encoding='utf-8-sig')

                            df_lag_list = np.array(df_lag).tolist()
                            if len(df_lag_list)>0:
                                sql_1 = 'insert ignore into detailresult(month,dRLI_t,dRLI_tt,RLI,Ta,DSH,label,SH,EV2,leak_prob,anomality,label_hour,mold,opemode,leakage,UNIT_ID,CREATE_DT) values(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)' 
                                operation_mysql.insertSql(sql_1,df_lag_list)
                            if len(detect_label)>0:
                                if detect_label=='leak':
                                    temp_value = round(df_lag[(df_lag['label_hour']==1)&(df_lag['label']==1)]['leakage'].mean(),2)
                                    leakage_temp=temp_value if not math.isnan(temp_value) else 0
                                    if leakage_temp>leakage_value:
                                        leakage_value=leakage_temp  
                                        if leakage_value<25:
                                            leakage_value=25
                                    check_result=detect_label
                                    list_Judge=[lc_number, check_result,opemode,leakage_value,pre_span,create_time]
                        if len(lc_number)>0:
                            list_Judge[0]=lc_number
                            list_label.append(list_Judge) 
                                
                    except Exception as e: 
                        print(e)
                        pass
                if len(list_label)>0:
                    sql_2 = 'insert ignore into judgementresult(UNIT_ID,detect_Label,opemode,leakage,leak_Dt,creat_Dt) values(%s,%s,%s,%s,%s,%s)' 
                    operation_mysql.insertSql(sql_2,list_label)
                    result_Judge = pd.DataFrame(data=list_label)
                    result_Judge.to_csv(resultpath+'/Judgement/Judge.csv',mode='a',header=False,encoding='utf-8-sig')
            
         

#☆
if __name__ == "__main__": 

    Begin_time = datetime.datetime.now()
    calcmode = 1
    pool = Pool(3)

    ivrv_check_list_single=['446','405','447'] 
    for modelcode in ivrv_check_list_single:
        calcmode =  calcmode if (modelcode!=1)&(modelcode in ['447']) else 1
        pool.apply_async(Check_RLI_clac_single,args=(modelcode,calcmode))
       
        # Check_RLI_clac_single(modelcode)

    ivrv_check_list_multi=['406','407','448','449','450']
    # ivrv_check_list_multi=['448']  
    for modelcode in ivrv_check_list_multi:
        pool.apply_async(Check_RLI_clac_multi,args=(modelcode,calcmode))
        # Check_RLI_clac_multi(modelcode) 
   
    pool.close()
    pool.join()
    End_time = datetime.datetime.now()
   
    # delectpath = os.path.abspath('') + r'/RLICalculationResult'
    # shutil.rmtree(delectpath)
    
    print('整个流程花费时间:'+ str(End_time-Begin_time))

    logging.basicConfig(level=logging.INFO,
                    filename=r'C:/Users/pb080086/Desktop/leakCheck.txt',
                    filemode='a',
                    format='%(asctime)s : %(message)s',
                    datefmt='%Y-%m-%d %H:%M:%S')
    
    logging.info('花费时间为'+str(End_time-Begin_time))
                 
