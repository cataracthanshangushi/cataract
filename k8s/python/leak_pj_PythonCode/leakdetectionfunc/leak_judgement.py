import numpy as np
import pandas as pd
import os
import glob

import matplotlib
matplotlib.use('Agg')
from matplotlib import pyplot as plt
import pickle
import sys
from sklearn.neighbors import LocalOutlierFactor
import datetime as dt
#import os
from os import path
#ファイル選択ダイアログ表示用追加 18/8/7
import tkinter.filedialog
import warnings
warnings.filterwarnings('ignore')  # warningを非表示にする
import xgboost as xgb

#河崎追加
# package for ML
from sklearn.model_selection import GridSearchCV
from sklearn.preprocessing import StandardScaler
#from sklearn.neural_network import MLPRegressor
from sklearn.ensemble import RandomForestRegressor
from sklearn.svm import SVR

import sys
sys.path.append(r'path/to/python module file')
basepath = path.dirname(path.abspath(__file__))
class leak_detection():
    def __init__(self, opeMode,w_size,n_JudgeDay,label_thr,scale,need_data_size,proba_thr,calcmode):
    
        """
        opemode:運転モード（冷房:1、暖房:2、str）
        w_size:スライド窓の大きさ 30は4日分データ量（1日8データ取得可と仮定）
        label_thr:異常度の閾値
        scale:ΔRLI振幅スケール
        need_data_size:分析に必要な最小限のデータサイズ（96データ（8データ×12日分）より大きい数字を設定）
        ※別機種でチューニングする際はw_size=30固定でlabel_thrだけ調整（⇒いずれ自動チューニングできるようにしたい）

        model_RFR.pickleはΔRLI(t,t+1)に写像した分類器のファイル (別コードで作成)
        """
        
        self.opeMode = opeMode
        self.w_size=w_size
        self.n_JudgeDay=n_JudgeDay
        self.label_thr=label_thr
        self.scale = scale
        # self.need_data_size = need_data_size
        self.calcmode = calcmode
        self.need_data_size_short = 80 if calcmode==1 else 30
        self.need_data_size_long=240
        self.proba_thr = proba_thr
        
        
        # load classifier that works for mapped ΔRLI(t, t+1) data 　#ΔRLI(t,t+1)を写像して分類する
        if opeMode == '1':
            # with open('./leakdetectionfunc/AutoDetect/hp_cool/model_xgb_new_20205181446.pickle', mode='rb') as fp:
            with open(basepath + r'/AutoDetect/hp_cool/model_RFR.pickle', mode='rb') as fp:
                self.lr = pickle.load(fp)
        elif opeMode == '2':
            with open(basepath + r'/AutoDetect/hp_heat/model_RFR.pickle', mode='rb') as fp:
                self.lr = pickle.load(fp)
        elif opeMode == '3':
            with open(basepath + r'/AutoDetect/hr_cool/model_RFR.pickle', mode='rb') as fp:
                self.lr = pickle.load(fp)
        elif opeMode == '4':
            with open(basepath + r'/AutoDetect/hr_heat/model_RFR.pickle', mode='rb') as fp:
                self.lr = pickle.load(fp)
    #ΔRLIの前処理
    def ΔRLI_cleansing(self, df_dRLI,need_data_size):
        """
        １．ΔRLIの外れ値除去
        ２．バイアス除去→min_maxスケーリング
        input: df
        output:df_dRLI_after : 前処理後
        """
        
        #データ準備
        df_dRLI_after = df_dRLI.copy(deep=True) #?deep=True

        #データ数が十分な時のみ処理
        if len(df_dRLI_after) > need_data_size: 
            if self.calcmode == 1:
                bias = df_dRLI_after.loc[0:need_data_size,'dRLI_RFR'].median() #?median*
            else:
                bias = 0
            #バイアス除去 coolの場合
            if self.opeMode == '1':
                df_dRLI_after.loc[:,'dRLI_RFR_after'] = 0
                if self.calcmode == 1:
                # バイアスが小さい場合
                    if abs(bias) < 0.02:
                        df_dRLI_after.loc[:,'dRLI_RFR_after'] = \
                            df_dRLI_after.loc[:,'dRLI_RFR'] - bias 
                    #バイアスが大きい場合
                    else:
                        df_dRLI_after.loc[:,'dRLI_RFR_after'] = df_dRLI_after.loc[:,'dRLI_RFR']
                else:
                    df_dRLI_after.loc[:,'dRLI_RFR_after'] = df_dRLI_after.loc[:,'dRLI_RFR'] * 0.4
                   
            #バイアス除去 heatの場合
            elif (self.opeMode == '2') or (self.opeMode == '3') or (self.opeMode == '4'):
                if self.calcmode == 1: 
                    df_dRLI_after.loc[:,'dRLI_RFR_after'] = df_dRLI_after.loc[:,'dRLI_RFR'] - bias
                else:
                    df_dRLI_after.loc[:,'dRLI_RFR_after'] = df_dRLI_after.loc[:,'dRLI_RFR'] * 0.02


            #バイアス除去 freeの場合
            # elif self.opeMode == '3':
            #     df_dRLI_after.loc[:,'dRLI_RFR_after'] = 0
            #     # バイアスが小さい場合
            #     if abs(bias) < 0.05:
            #         df_dRLI_after.loc[:,'dRLI_RFR_after'] = \
            #             df_dRLI_after.loc[:,'dRLI_RFR'] - bias 
            #     #バイアスが大きい場合
            #     else:
            #        df_dRLI_after.loc[:,'dRLI_RFR_after'] = df_dRLI_after.loc[:,'dRLI_RFR']
                   
                   
            #min_maxスケーリング
            drli_min = df_dRLI_after.loc[0:need_data_size,'dRLI_RFR_after'].min()
            drli_max = df_dRLI_after.loc[0:need_data_size,'dRLI_RFR_after'].max()
            if drli_max-drli_min != 0:
                if self.calcmode == 1:
                    df_dRLI_after.loc[:,'dRLI_RFR_after'] =\
                        df_dRLI_after.loc[:,'dRLI_RFR_after']*self.scale/abs(drli_max-drli_min)
                else:
                    pass
            else:pass
        else:pass
                       
        df_dRLI_after = df_dRLI_after.reset_index(drop=True)
        return df_dRLI_after

    #DataFrame作成
    def create_dataframe(self,df_dRLI_after):
        """
        input: df_dRLI_after
        output:df_lag : t,t+1の特徴量作成

        df_lag：N行３列のデータフレーム（column：日時、ΔRLI(t)、ΔRLI（t+1））
        """
        df_lag=pd.DataFrame(columns = \
            ['datetime','month','dRLI_t','dRLI_t+1','RLI','Ta','DSH','label'])
        df_lag.loc[:,'datetime'] = df_dRLI_after['Datetime']
        df_lag.loc[:,'month'] = df_dRLI_after['Datetime'].dt.month
        df_lag.loc[:,'dRLI_t']    = df_dRLI_after.loc[:,'dRLI_RFR_after']
        df_lag.loc[:,'dRLI_t+1']  = df_dRLI_after.loc[:,'dRLI_RFR_after'].shift(1)
        df_lag.loc[:,'RLI'] = df_dRLI_after.loc[:,'RLI_ori']
        df_lag.loc[:,'Ta'] = df_dRLI_after.loc[:,'OU1Ta']
        df_lag.loc[:,'DSH'] = df_dRLI_after.loc[:,'OU1Td']-df_dRLI_after.loc[:,'OU1Tc']
        df_lag.loc[:,'SH'] = df_dRLI_after.loc[:,'SHsuc']
        df_lag.loc[:,'EV2'] = df_dRLI_after.loc[:,'OU1EV2pls']
        # df_lag.loc[:,'OU1ThexLiq'] = df_dRLI_after.loc[:,'外1Thex出']
        df_lag = df_lag.loc[1:,:].reset_index(drop=True)
     
        return df_lag

    #予測
    def model_predict(self, df_lag):
        """
        df_lag[['ΔRLI_t','ΔRLI_t+1']]の特徴量をモデルself.lrに入力して予測⇒df_lag['label']
        input: df_lag
        output:df_lag : df['label']追加
        """
        # if self.opeMode == '1':
        #     columns = ['month','dRLI_t','dRLI_t+1','RLI','Ta','DSH']
        #     X_test = np.array(df_lag.loc[:,columns])
        #     dtest = xgb.DMatrix(X_test)
        #     y_pred_proba = self.lr.predict(dtest)
        #     y_pred = np.where(y_pred_proba > self.proba_thr, 1, 0)
        #     df_lag.loc[:,'label'] = y_pred

        # elif (self.opeMode == '1')or(self.opeMode == '2')or(self.opeMode == '3')or(self.opeMode == '4'):
        
        columns = ['dRLI_t','dRLI_t+1']
        X_test = df_lag[columns]
        # y_pred = self.lr.predict(X_test)
        # df_lag['label'] = y_pred
        
        # 0，1判定を指定した閾値から決めれるように。
        # calculate leak probability
        probs = self.lr.predict_proba(X_test)[:, 1]
    
        # add new column
        df_lag['leak_prob'] = probs
        # change threshold for leak judgement
        y_pred = (probs >= self.proba_thr).astype(int)
        # add new column
        df_lag['label'] = y_pred
        
        return df_lag

    #異常度計算⇒二値分類
    def judgement(self, df_lag):
        """
        スライド窓中のデータが異常領域に入る確率を異常度とする。
        異常度がlabel_thrをこえたら異常（ラベル１）とする。
        input:df_lag,label_thr
        output:df_lag(時報データ),df_leak(日データ)
        """
        #窓幅で予測したラベルの移動平均とる
        df_lag['anomality'] = df_lag['label'].rolling(self.w_size).mean()
        df_lag = df_lag.dropna()
        '''
        # 閾値で分けたときの０，１を入力する列を準備
        # df_lag['label_thr']=df_lag['label']
        df_lag.loc[df_lag['label_move'] >self.label_thr,'label_thr']=1
        df_lag.loc[df_lag['label_move'] <= self.label_thr,'label_thr']=0
        df_lag.set_index('datetime', inplace=True)
        #予測したラベルの日次平均とる
        df_leak = df_lag[['label_thr']]
        df_leak = df_leak.resample("D").mean() #日時平均
        df_leak = df_leak.dropna()
        df_leak[df_leak > 0.7] = 1 #半日以上異常だとその日は異常 0.5->0.7に変更（2020/04/21）
        df_leak[df_leak <= 0.7] = 0
        df_leak = pd.DataFrame(df_leak,columns=["label_thr"])
        '''
        # 閾値で分けたときの０，１を入力する列を準備
        df_lag.set_index('datetime', inplace=True)
        #予測したラベルの日次平均とる
        HourlyLeak = df_lag[['anomality']]
        DailyLeak = HourlyLeak.resample("D").mean() #日平均
        df_leak_day = DailyLeak.dropna()
  
        df_leak_day[df_leak_day > self.label_thr] = 1
        df_leak_day[df_leak_day <= self.label_thr] = 0
        df_leak_day = pd.DataFrame(df_leak_day)
        df_leak_day.columns=["label_day"]
   
        df_leak_day["anomality"]=DailyLeak.dropna()
     
        
        # 時間ごとの異常度からの判定を同じ閾値のループで
        HourlyLeak[HourlyLeak > self.label_thr] = 1 
        HourlyLeak[HourlyLeak <= self.label_thr] = 0
        df_lag['label_hour']=HourlyLeak
   
        return df_lag, df_leak_day

            
    def get_leak_or_not__and_span(self, df_lag, df_leak_day):
        """
        データ内に故障があるかないかを判定し、ある場合その期間を取得する
        :param df_leak : ld.run(df)済のデータフレーム
        :return detect_label : 漏洩があるかないか 0:漏洩無し 1:漏洩あり
        :return pre_span : 低下期間 複数ある場合でも1つのstring変数にまとめて返す
        """
        # 低下と判断された日数のカウント
        # 2日連続、10日の内に3,5,7回異常判定、単純な全期間カウント、
  
        leak_hours = np.sum(df_lag["label_hour"])
        if leak_hours <= self.n_JudgeDay:
            
            # 低下日数が0の場合判定ラベルを0（正常）にする
            detect_label = 0
            pre_span = ""  
        else:
          
            # 低下が1日でもある場合判定ラベルを1（低下）にする
            detect_label = 1
            pred = df_leak_day["label_day"]
       

            #######低下開始と終了の日を取得する######

            # ラベル切り替わり点の取得
            switch_point = np.array([])
            plus_or_minus = np.array([])  # -1:低下→正常　or 1:正常→低下　#i-1:iの1日前
            for i in range(1, len(pred)):
                diff = pred[i] - pred[i-1]
                if diff != 0:
                    switch_point = np.append(switch_point, i)
                    plus_or_minus = np.append(plus_or_minus, diff)
        

            # 切り替わり点の日時を取得 
            # 1つの列に低下期間が複数ある場合でもすべて入れる
            pre_span = ""
            write_flag = 1
            for i in range(len(switch_point)):
                if plus_or_minus[i] == 1: 
                    # 低下開始
                    if write_flag == 1:
                        pre_span = pre_span + str(df_leak_day.index[int(switch_point[i])].year) \
                                   + "年" + str(df_leak_day.index[int(switch_point[i])].month) + "月～"
                elif plus_or_minus[i] == -1:
                    if i == 0:
                        # データが低下から始まっている場合
                        pre_span = "～" + pre_span
                    # 低下終了
                    if i != len(switch_point) - 1 and df_leak_day.index[int(switch_point[i + 1])].year \
                            == df_leak_day.index[int(switch_point[i])].year \
                            and df_leak_day.index[int(switch_point[i + 1])].month \
                            - df_leak_day.index[int(switch_point[i])].month <= 1:
                        # 低下期間が同月にある場合 or 連続した月にある場合 は記載せず、期間をつなげる
                        write_flag = 0
                    elif i != len(switch_point) - 1 and df_leak_day.index[int(switch_point[i + 1])].year \
                            - df_leak_day.index[int(switch_point[i])].year == 1 \
                            and df_leak_day.index[int(switch_point[i + 1])].month \
                            - df_leak_day.index[int(switch_point[i])].month == -11:
                        # 上と同様だが期間が年をまたいで連続で存在する場合
                        write_flag = 0
                    else:
                        write_flag = 1

                    if write_flag == 1:  
                        pre_span = pre_span + str(df_leak_day.index[int(switch_point[i] - 1)].year) \
                                   + "年" + str(df_leak_day.index[int(switch_point[i] - 1)].month) + "月 / "
                # pre_span=""
        return detect_label, pre_span


    def run(self,df_dRLI):
        df_dRLI['Datetime'] = pd.to_datetime(df_dRLI['Datetime'])
        df_dRLI['Datetime'] = df_dRLI['Datetime'].dt.round("H") #１時間間隔に丸める
        if len(df_dRLI) > self.need_data_size_short:
            df_dRLI = df_dRLI[['Datetime','Ctrlmode','RLI_ori','dRLI_RFR','OU1Ta',\
                               'OU1Td','OU1Tc','SHsuc','OU1EV2pls','OU1ThexLiq']].dropna()
            df_dRLI['DSH'] = df_dRLI['OU1Td'] - df_dRLI['OU1Tc']

            # オフセットを更新
            # df_dRLI_after = self.ΔRLI_cleansing(df_dRLI)
            if len(df_dRLI) < self.need_data_size_long:
                df_dRLI_after = self.ΔRLI_cleansing(df_dRLI,self.need_data_size_short)
            elif len(df_dRLI) >= self.need_data_size_long:
                df_dRLI_und200 = df_dRLI[0:self.need_data_size_long]
                df_dRLI_ove200 = df_dRLI
                df_dRLI_und200_after = self.ΔRLI_cleansing(df_dRLI_und200,self.need_data_size_short)
                df_dRLI_ove200_after = self.ΔRLI_cleansing(df_dRLI_ove200,self.need_data_size_long)
                    
                df_dRLI_ove200_after = df_dRLI_ove200_after[self.need_data_size_long:]
                df_dRLI_after = pd.concat([df_dRLI_und200_after,df_dRLI_ove200_after])

            df_lag = self.create_dataframe(df_dRLI_after.copy())
            df_lag = self.model_predict(df_lag)

            # 窓幅分を確保
            if len(df_lag) > self.w_size:
                df_lag, df_leak_day = self.judgement(df_lag)
            else:
                print("Data is insufficient. {}".format(len(df_lag)))
        else:   
            print("Data is insufficient. {}".format(len(df_dRLI)))
               
            df_leak_day=df_dRLI_after=detect_label=pre_span = df_lag=''
        
        #?
        if type(df_leak_day) == str: 
            print('')
        else:
            detect_label, pre_span = self.get_leak_or_not__and_span(df_lag, df_leak_day)

        return df_leak_day, df_lag, df_dRLI_after,detect_label,pre_span  
    