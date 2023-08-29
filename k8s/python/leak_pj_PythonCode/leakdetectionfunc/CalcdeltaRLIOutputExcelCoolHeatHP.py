# -*- coding: utf-8 -*-
# calc deltaRLI using ML model, Output to xlsx files, for cooling and heating
"""
Created on Thu Jun 8 11:14:12 2019

@author: 

Change log:
20/09/14:Modification in moving average, the number of data in the moving average was previously fixed at 20, but it can be changed now.
20/09/14:Modification of ML model for V5 series, adding capacities as well as model codes to the parameters of  the classification of machine learning models.
20/09/14:Modification in data cleansing, delete dataset at suction SH < 1 in heating mode by the request from Hikawa-san
"""

import numpy as np
import pandas as pd
# add comment to Excel file 19/9/26

import glob
# package for ML
from sklearn.preprocessing import StandardScaler

# add for serialize 18/10/1
import joblib
import pickle

from os import path
import sys

# display dialog box for selecting folder 18/8/7
import tkinter, tkinter.filedialog

#河崎追加（モデル用）
# package for ML
from sklearn.model_selection import GridSearchCV
from sklearn.preprocessing import StandardScaler
#from sklearn.neural_network import MLPRegressor
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestRegressor
from xgboost import XGBRegressor
from sklearn.svm import SVR
import xgboost as xgb
import lightgbm as lgb


# data cleansing and get model code, horse power (arguments are excel file paths and operation mode)
def data_cleansing(df_vrv, modelcode, opemode):

    df_vrv = df_vrv.set_index('Datetime')
    if opemode == '1':
            # delete outlier for all model code
        df_vrv[df_vrv['OU1Ta'] > 45] = np.nan
       
        df_vrv[df_vrv['OU1Te'] < -4] = np.nan 
       
        df_vrv[df_vrv['Ctrlmode'] != 4] = np.nan    # normal cooling mode
        
        df_vrv[df_vrv['SHsuc'] < 0] = np.nan
        
        df_vrv[df_vrv['RLI(Te)'] < 0] = np.nan     # delete dataset at RLI<0, sometimes Tc<Tb
      
        df_vrv[df_vrv['RLI(Te)'] > 0.25] = np.nan
       

        if modelcode == '306':
            #V3R 14-16HP(1 INV comp in OU1 and OU2),外れ値判定条件は神田淡路町ビル他の運転データ分析からの仮決め
            df_vrv[(df_vrv['OU1INVrps'] <= 15) & (df_vrv['OU2INV2rps'] <= 15)] = np.nan
            df_vrv[(df_vrv['OU1INVCopt'] < 20) & (df_vrv['OU2INVCopt'] < 20)] = np.nan
            df_vrv[df_vrv['OU2INVCOnOffN'] >= 4] = np.nan
        #221012 add 406 407 446
        elif modelcode == '368' or modelcode == '390' or modelcode == '401' or \
            modelcode == '405' or modelcode == '406' or modelcode == '407' or modelcode == '446'or modelcode == '415' or modelcode == '419' or \
            modelcode == '443':
            # V3B,V4,V4R,V5R 14-16HP(2 INV comp in OU1)
            df_vrv[(df_vrv['OU1INVrps'] <=15) & (df_vrv['OU1INV2rps'] <= 15)] = np.nan
            df_vrv[(df_vrv['OU1INVCopt'] < 20) & (df_vrv['OU1INV2Copt'] < 20)] = np.nan
            df_vrv[df_vrv['OU1INV2COnOffN'] >= 4] = np.nan
        else:
            # V3A/V4 5-8HP,V5,V3R,V4R,V5R 8-12HP(1 INV comp in OU1)
            df_vrv[df_vrv['OU1INVrps'] <= 15] = np.nan
      
            # df_vrv[df_vrv['OU1INVCopt'] < 20] = np.nan
    
        df_vrv = df_vrv[df_vrv['OU1Ta'].notnull()]  


    elif opemode == '2':
        df_vrv[df_vrv['OU1Te'] < -20] = np.nan       #-4 -> -20 に変更 for DENV(delete startup data, percent of delete data is 12.7% in LC8NG32767-1-2-39)
        df_vrv[df_vrv['OU1INVCOnOffN'] > 5] = np.nan #冷房と同条件 1時間に4回以上の発停
        df_vrv[df_vrv['Ctrlmode'] != 19] = np.nan   # 冷房と同条件 normal heating mode
        df_vrv[df_vrv['RLI(Rp)'] < 0] = np.nan#冷房と同条件 delete dataset at RLI<minRLI by yamada's theory
        df_vrv[df_vrv['OU1INVCopt'] < 10] = np.nan
        
        df_vrv['IU_TON_Cap']=df_vrv['IU_TON_Cap']/(df_vrv['OU1HP']*3.125)# Covert Thermo ON Capacity to percent value dividing HorsePower
        #馬力の約3.125倍が暖房能力を表すため。
        df_vrv = df_vrv[df_vrv['OU1Ta'].notnull()]

        
    elif opemode=="3":
        df_vrv[(df_vrv['OU1Tc']-df_vrv['OU1Ta']) < 3] = np.nan #データ数　2%減
        # df_vrv[df_vrv['外1Fan回転数'] == 0] = np.nan #データ数　30%減
        df_vrv[df_vrv['SHsuc'] < 10] = np.nan #データ数　2%減
        df_vrv[df_vrv['OU1Te'] < -15] = np.nan #データ数　7%減
        df_vrv[df_vrv['OU1INVCOnOffN'] > 5] = np.nan #冷房と同条件 1時間に4回以上の発停
        df_vrv[df_vrv['OU1INVCopt'] < 10] = np.nan
        df_vrv[df_vrv['RLI(Tsc)'] < 0] = np.nan     # delete dataset at RLI<0, sometimes Tc<Tb
        df_vrv[df_vrv['RLI(Tsc)'] > 0.25] = np.nan
        # df_vrv[df_vrv['CVRLI'] > 1] = np.nan
        df_vrv['IU_TON_Cap']=df_vrv['IU_TON_Cap']/(df_vrv['OU1HP']*2.8)
        df_vrv = df_vrv[df_vrv['OU1Ta'].notnull()]
    
    elif opemode=="4":
        df_vrv[(df_vrv['OU1Ta']-df_vrv['OU1Te']) < 2] = np.nan 
        # df_vrv[df_vrv['外1Fan回転数'] == 0] = np.nan #データ数　30%減
        df_vrv[df_vrv['SHsuc'] < 0] = np.nan #データ数　2%減
        df_vrv[df_vrv['OU1Te'] < -20] = np.nan #データ数　7%減
        df_vrv[df_vrv['OU1INVCOnOffN'] > 5] = np.nan #冷房と同条件 1時間に4回以上の発停
        df_vrv[df_vrv['OU1INVCopt'] < 10] = np.nan
        df_vrv['IU_TON_Cap']=df_vrv['IU_TON_Cap']/(df_vrv['OU1HP']*2.8)
        df_vrv = df_vrv[df_vrv['OU1Ta'].notnull()]
    return df_vrv


# extract model input and output(RLI) from dataset
def extract_modelIO(df_vrv, modelcode, opemode,calcmode):
    x_df=pd.DataFrame()
    y_df=pd.DataFrame()
    if opemode == '1':
        if modelcode == '284':
            # V3A 10-12HP(1 STD comp)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'Comp2'], \
            x_df.loc[:, 'EV2'], x_df.loc[:, 'Current'], x_df.loc[:, 'Tc'] = \
            df_vrv.loc[:, 'OU1Ta'], df_vrv.loc[:, 'OU1INVrps'], \
            df_vrv.loc[:, 'OU1STDC1'], df_vrv.loc[:, 'OU1EV2pls'], \
            df_vrv.loc[:, 'OU1INVCur'], df_vrv.loc[:, 'OU1Tc']
        elif modelcode == '285':
            # V3A 14-18HP(2 STD comp)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'Comp2'], \
            x_df.loc[:, 'Comp3'], x_df.loc[:, 'EV2'], x_df.loc[:, 'Current'], \
            x_df.loc[:, 'Tc'] = \
            df_vrv.loc[:, 'OU1Ta'], df_vrv.loc[:, 'OU1INVrps'], \
            df_vrv.loc[:, 'OU1STDC1'], df_vrv.loc[:, 'OU1STDC2'], \
            df_vrv.loc[:, 'OU1EV2pls'], df_vrv.loc[:, 'OU1INVCur'], \
            df_vrv.loc[:, 'OU1Tc']
        elif modelcode == '305':
            # V3R 8-12HP(1 STD comp in OU2)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'O2Comp'], \
            x_df.loc[:, 'EV2'], x_df.loc[:, 'O2EV2'], x_df.loc[:, 'Current'],
            x_df.loc[:, 'Tc'] = \
            df_vrv.loc[:, 'OU1Ta'], df_vrv.loc[:, 'OU1INVrps'], \
            df_vrv.loc[:, 'OU2STDC1'], df_vrv.loc[:, 'OU1EV2pls'], \
            df_vrv.loc[:, 'OU2EV2pls'], df_vrv.loc[:, 'OU1INVCur'], \
            df_vrv.loc[:, 'OU1Tc']
        elif modelcode == '306':
            # V3R 14-16HP(1 INV comp in OU2)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'O2Comprps'], \
            x_df.loc[:, 'EV2'], x_df.loc[:, 'O2EV2'], x_df.loc[:, 'Current1'], \
            x_df.loc[:, 'O2Current'], x_df.loc[:, 'Tc'] = df_vrv.loc[:, 'OU1Ta'], \
            df_vrv.loc[:, 'OU1INVrps'], df_vrv.loc[:, 'OU2INV2rps'], \
            df_vrv.loc[:, 'OU1EV2pls'], df_vrv.loc[:, 'OU2EV2pls'], \
            df_vrv.loc[:, 'OU1INVCur'], df_vrv.loc[:, 'OU2INV2Cur'], \
            df_vrv.loc[:, 'OU1Tc']
        elif modelcode == '368' or modelcode == '390' or modelcode == '401' or \
            modelcode == '405' or modelcode == '406' or modelcode == '407' or modelcode == '446'or  modelcode == '415' or modelcode == '419' or \
            modelcode == '443':
            # V3B,V4,V4R,V5R 14-16HP(2 INV comp in OU1)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'Comp2rps'], \
            x_df.loc[:, 'EV2'], x_df.loc[:, 'Current1'], x_df.loc[:, 'Current2'], \
            x_df.loc[:, 'Tc'] = \
            df_vrv.loc[:, 'OU1Ta'], df_vrv.loc[:, 'OU1INVrps'], \
            df_vrv.loc[:, 'OU1INV2rps'], df_vrv.loc[:, 'OU1EV2pls'], \
            df_vrv.loc[:, 'OU1INVCur'], df_vrv.loc[:, 'OU1INV2Cur'], \
            df_vrv.loc[:, 'OU1Tc']
        else:
            #V3A/V4 5-8HP,V5,V4R,V5R 8-12HP(1 INV comp in OU1)
            x_df.loc[:, 'Ta'], x_df.loc[:, 'Comp1rps'], x_df.loc[:, 'EV2'], \
            x_df.loc[:, 'Current'], x_df.loc[:, 'Tc'] = df_vrv.loc[:, 'OU1Ta'], \
            df_vrv.loc[:, 'OU1INVrps'], df_vrv.loc[:, 'OU1EV2pls'], \
            df_vrv.loc[:, 'OU1INVCur'], df_vrv.loc[:, 'OU1Tc']
        y_df.loc[:, 'RLI'] = df_vrv.loc[:, 'RLI(Te)']
    
    elif opemode == '2':
        # x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
        x_df.loc[:, 'INVrps'] = df_vrv.loc[:, 'OU1INVrps']
        x_df.loc[:, 'TONcap'] = df_vrv.loc[:, 'IU_TON_Cap']
        x_df.loc[:, 'EV2'] = df_vrv.loc[:, 'OU1EV2pls']
        x_df.loc[:, 'Ta'] = df_vrv.loc[:, 'OU1Ta']
        if calcmode!=1:
            x_df.loc[:, 'Tsh'] = df_vrv.loc[:, 'OU1Tsh']
            x_df.loc[:, 'ThexLiq'] = df_vrv.loc[:, 'OU1ThexLiq']
            x_df.loc[:, 'TLiqPipe'] = df_vrv.loc[:, 'OU1TLiqPipe']
            x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
        else:pass
        if modelcode=='390':
            x_df.loc[:, 'INVrps2'] = df_vrv.loc[:, 'OU1INV2rps']
        y_df.loc[:, 'RLI'] = df_vrv.loc[:, 'SHdis']
            
    elif opemode == '3': # HR kawasaki program による パラメータに変更
        if modelcode=='414':
            x_df.loc[:, 'Te'] = df_vrv.loc[:, 'OU1Te']
            x_df.loc[:, 'Tc'] = df_vrv.loc[:, 'OU1Tc']
            x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
            x_df.loc[:, 'EV2'] = df_vrv.loc[:, 'OU1EV2pls']
            x_df.loc[:, 'EV3'] = df_vrv.loc[:, 'OU1EV3pls']
            y_df.loc[:, 'RLI'] = df_vrv.loc[:, 'RLI(Tsc)']
        elif modelcode=='442' or modelcode=='418' or modelcode=='415' or modelcode=='419':
            x_df.loc[:, 'INVrps'] = df_vrv.loc[:, 'OU1INVrps']
            x_df.loc[:, 'TONcap'] = df_vrv.loc[:, 'IU_TON_Cap']
            x_df.loc[:, 'Te'] = df_vrv.loc[:, 'OU1Te']
            # x_df.loc[:, 'Pe'] = df_vrv.loc[:, 'OU1Pe']
            x_df.loc[:, 'Tc'] = df_vrv.loc[:, 'OU1Tc']
            # x_df.loc[:, 'Pc'] = df_vrv.loc[:, 'OU1Pc']
            x_df.loc[:, 'SH'] = df_vrv.loc[:, 'SHsuc']
            x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
            x_df.loc[:, 'EV2'] = df_vrv.loc[:, 'OU1EV2pls']
            x_df.loc[:, 'EV3'] = df_vrv.loc[:, 'OU1EV3pls']
            x_df.loc[:, 'Ta'] = df_vrv.loc[:, 'OU1Ta']  
            y_df.loc[:, 'RLI'] = df_vrv.loc[:, 'RLI(Tsc)']
                
    elif opemode == '4': # HR kawasaki program による パラメータに変更
        if modelcode=='414':
            x_df.loc[:, 'INVrps'] = df_vrv.loc[:, 'OU1INVrps']
            x_df.loc[:, 'TONcap'] = df_vrv.loc[:, 'IU_TON_Cap']
            x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
            x_df.loc[:, 'EV2'] = df_vrv.loc[:, 'OU1EV2pls']
            x_df.loc[:, 'EV3'] = df_vrv.loc[:, 'OU1EV3pls']
            x_df.loc[:, 'Ta'] = df_vrv.loc[:, 'OU1Ta']
        elif modelcode=='415':
            x_df.loc[:, 'INVrps'] = df_vrv.loc[:, 'OU1INVrps']
            x_df.loc[:, 'TONcap'] = df_vrv.loc[:, 'IU_TON_Cap']
            # x_df.loc[:, 'Te'] = df_vrv.loc[:, 'OU1Te']
            # x_df.loc[:, 'Tc'] = df_vrv.loc[:, 'OU1Tc']
            # x_df.loc[:, 'EV1'] = df_vrv.loc[:, 'OU1EV1pls']
            x_df.loc[:, 'EV2'] = df_vrv.loc[:, 'OU1EV2pls']
            x_df.loc[:, 'EV3'] = df_vrv.loc[:, 'OU1EV3pls']
            # x_df.loc[:, 'Ta'] = df_vrv.loc[:, 'OU1Ta']
            x_df.loc[:, 'INVrps2'] = df_vrv.loc[:, 'OU1INV2rps']
        y_df.loc[:, 'RLI'] = df_vrv.loc[:, 'SHdis']
    return x_df, y_df


# calculate moving average
def get_movingave(df_vrv, numave):
    df_mvave = df_vrv.rolling(window = numave).mean()   # simple moving average
    df_mvave = df_mvave[df_mvave['OU1Ta'].notnull()]    # delete null data
    return df_mvave

# load standardizing model and ML model
def load_stdMLModel(basepath, opemode, modelcode, coolheat, horsepower,calcmode):
    # for V5,V5R,V6,V6R(1 INV comp in OU1)
    # if opemode=='2':
    #     modelfilepath = basepath + r'/RLIMLTrainData/VRV/' + coolheat + r'/' + modelcode + "_ExParams_DSH"
    #     modelfileext = modelcode+'-'+str(int(horsepower)) + 'ExParamsDSH.pickle'
        
    #     # load standardizing model
    #     modelfile = modelfilepath + '/_' + coolheat + '_stdscale' + modelfileext
    #     with open(modelfile, "rb") as fil:
    #         stdscale = pickle.load(fil)
            
    #     # load ML model
    #     modelfile = modelfilepath + '/_' + coolheat + '_tunedLGBMModel' + modelfileext
    #     with open(modelfile, "rb") as fil:
    #         MLmodel = pickle.load(fil)
            
    # elif opemode=="3" or opemode=="4":
    modelfilepath = basepath + r'\MLModel\CalcdRLIModel/VRV/' + coolheat + r'/' + modelcode
    # modelfilepath = basepath + r'\MLModel\CalcdRLIModel/VRV/' + coolheat + r'/' + '424'
    # load standardizing model
    if calcmode == 1:
        modelfile = modelfilepath + '/_' + coolheat + '_stdscale' +'-'+ str(int(horsepower)) +'.pickle'
    else:
        modelfile = modelfilepath + '/_' + coolheat + '_stdscale_special' +'-'+ str(int(horsepower)) +'.pickle'
    with open(modelfile, "rb") as fil:
        stdscale = pickle.load(fil)
        
    # load ML model
    if calcmode == 1:
        modelfile = modelfilepath + '/_' + coolheat + '_MLmodel' +'-'+ str(int(horsepower))  +'.pickle'
    else:
        modelfile = modelfilepath + '/_' + coolheat + '_MLmodel_special' +'-'+ str(int(horsepower))  +'.pickle'
    with open(modelfile, "rb") as fil:
        MLmodel = pickle.load(fil)
            
    return stdscale, MLmodel#, XGBmodel

# calc predict RLI value by each ML model (arguments are ML model name, model input X, name of DataFrame for index)
def get_predictRLI(model, X_dfstd, Y_df, X_df, opemode):
    predictRLI = model.predict(X_dfstd)         # get predicted RLI for X_df using ML model
    result = pd.DataFrame(index=X_df.index)     # subsutitute index of X_df to result
    # result.loc[:, 'Ta'] = X_df.loc[:, 'Ta']     # copy Ta to result from X_df
    result.loc[:, 'predict'] = predictRLI       # merge predicted RLI to result(column 'predict')
    if (opemode=='1') or (opemode =='3'):
        result.loc[:, 'dRLI'] = (Y_df - predictRLI)   # calc deltaRLI
    elif (opemode =='2') or (opemode =='4'):
        result.loc[:, 'dRLI'] = (-1)*(Y_df - predictRLI)   # calc deltaRLI
    result = result[result.loc[:, 'predict'].notnull()] # delete null data
    return result


#####################MAIN############################

def main(df,modelcode,horsepower,opemode,calcmode):
    #☆河崎追加
    #仮置き(移動平均させる数。湖底にするか入力にするか用検討)
    nummvave = 1
    # get own file path
    basepath = path.dirname(path.abspath(__file__))
    print(basepath)
    # get file path of test data files
    if opemode == '1':
        coolheat = 'hp_cool'
    elif opemode == '2':
        coolheat = 'hp_heat'
    elif opemode == '3':
        coolheat = 'hr_cool'
    elif opemode == '4':
        coolheat = 'hr_heat'

    # instance for standardizing
    stdsc = StandardScaler()
    
    # data cleansing and get model code for test data
    df_test = data_cleansing(df, modelcode, opemode)

    # no processing if number of data < 10 (too small data aren't good for prediction)
    if len(df_test) > 10:
        # load standardizing model and ML model for predicting RLI
        stdscale, MLmodel = load_stdMLModel(basepath,opemode, modelcode, coolheat, horsepower,calcmode)
        # calculate moving average (num of MA=nummnave, good for reducing noise)
        df_testmvave=get_movingave(df_test, int(nummvave))
        # standardizing test data
        df_test = df_testmvave.copy() #reset_index(drop = True)  # delete invalid data and reset index
        X_df_test, Y_df_test = extract_modelIO(df_test, modelcode, opemode,calcmode)  # extract X,Y(RLI) from dataset
        X_df_teststd = stdscale.transform(X_df_test)    # standardizing X
        Y_df_test = Y_df_test.values.flatten()          # flattening Y(RLI)   
        # get predicted RLI and deltaRLI for each ML model
        # df_rsltMLP = get_predictRLI(MLPmodel, X_df_teststd, Y_df_test, X_df_test)
        df_rsltRFR = get_predictRLI(MLmodel, X_df_teststd, Y_df_test, X_df_test,opemode)
        # df_rsltXGB = get_predictRLI(XGBmodel, X_df_teststd, Y_df_test, X_df_test)
        
        # output cool/heatRLI.xlsx using pandas
        result = pd.DataFrame(index=df_test.index)

        if opemode == '1':
            result.loc[:, 'Ta'], result.loc[:, 'RLI_ori'], \
                result.loc[:, 'RLI_RFR'], result.loc[:, 'dRLI_RFR'] = \
                df_test.loc[:, 'OU1Ta'], df_test.loc[:, 'RLI(Te)'], \
                df_rsltRFR.loc[:, 'predict'], df_rsltRFR.loc[:, 'dRLI']
        elif opemode == '2':
            result.loc[:, 'Ta'], result.loc[:, 'RLI_ori'], \
                result.loc[:, 'RLI_RFR'], result.loc[:, 'dRLI_RFR'] = \
                df_test.loc[:, 'OU1Ta'], df_test.loc[:, 'SHdis'], \
                df_rsltRFR.loc[:, 'predict'], df_rsltRFR.loc[:, 'dRLI']
        elif opemode == '3':
            result.loc[:, 'RLI_ori'], \
                result.loc[:, 'RLI_RFR'], result.loc[:, 'dRLI_RFR'] = \
                df_test.loc[:, 'RLI(Tsc)'], \
                df_rsltRFR.loc[:, 'predict'], df_rsltRFR.loc[:, 'dRLI']
        elif opemode == '4':
            result.loc[:, 'Ta'], result.loc[:, 'RLI_ori'], \
                result.loc[:, 'RLI_pred'], result.loc[:, 'dRLI_RFR'] = \
                df_test.loc[:, 'OU1Ta'], df_test.loc[:, 'SHdis'], \
                df_rsltRFR.loc[:, 'predict'], df_rsltRFR.loc[:, 'dRLI']
        return_df = pd.concat([df_test, result], axis=1)
        return return_df
