# -*- coding: utf-8 -*-
"""
Created on Mon Nov  8 13:58:34 2021

@author: kimurashu
"""
import seaborn as sns
import math
import matplotlib.pyplot as plt
import pandas as pd
color = sns.color_palette()
from sklearn.metrics import confusion_matrix
from sklearn.metrics import accuracy_score


def save_matrics_figure(resultpath,filename_calc,list_label,df_answer,span_list):
    # LC_numberが一致した人間判定の結果をlist_labelに追加
    df_label = pd.DataFrame(list_label, columns=['LC_number', 'detect_label','umapower'])
    df_label['漏洩期間'] = span_list
    df_answer = df_answer[['LC_number','人間判定']]
    df_merge = pd.merge(df_label, df_answer, on="LC_number")
    
    # df_merge = df_merge.replace({'正常':'normal'},{'漏洩':'leak'},{'湿り':'wet'})     
    df_merge = df_merge.replace({'正常':'normal'})   
    df_merge = df_merge.replace({'漏洩':'leak'}) 
    # df_merge = df_merge.replace({'正常（保留）':'normal'})   
    # df_merge = df_merge.replace({'漏洩（保留）':'leak'}) 
    df_merge = df_merge.replace({'湿り':'wet'}) 
    df_merge = df_merge.replace({'保留':'pending'}) 
    
    df_merge = df_merge[df_merge['detect_label'] != 'wet']
    df_merge = df_merge[df_merge['人間判定'] != 'wet']
    df_merge = df_merge[df_merge['人間判定'] != 'pending']
    
    #列順入れ替え
    df_merge = df_merge[['LC_number','detect_label','人間判定','umapower','漏洩期間']]
    print(df_merge)
    df_merge.to_excel(resultpath+'/Judgement/' + filename_calc+'_judge.xlsx', sheet_name='new_sheet_name')
    
    predict = df_merge['detect_label'].to_list()
    answer = df_merge['人間判定'].to_list()
    
    #正解が正常漏洩以外のデータを削除
    del_list=[]
    for j,ans in enumerate(answer):
        if ans == 'normal' :continue
        elif ans == 'leak':continue
        else:del_list.append(j)
    for i in sorted(del_list, reverse=True):
        answer.pop(i)
        predict.pop(i)


    #正答率と誤判定率を算出し、混合行列を出力
    cm = confusion_matrix(predict, answer)
    #誤判定率と精度を出力　変数名：ans_pred 正常：nml 異常：abn 
    #ラベル　0:異常　1:正常
    nml_nml=cm[1,1]
    abn_abn=cm[0,0]
    abn_nml=cm[0,1]
    nml_abn=cm[1,0]
    sum_abn = abn_abn + nml_abn
    sum_nml = abn_nml + nml_nml
    abnRate = ((abn_nml/sum_nml) / (abn_abn/sum_abn + abn_nml/sum_nml)).round(3)
    # 誤検知率=nanの時、0にする
    if math.isnan(abnRate):
        abnRate = 0.0
    accuracy = ((abn_abn/sum_abn + nml_nml/sum_nml)/(abn_abn/sum_abn + abn_nml/sum_nml + nml_abn/sum_abn + nml_nml/sum_nml)).round(3)
    
    #混合行列を出力
    plt.figure(figsize=(5, 4))
    sns.heatmap(cm, annot=True, cmap='Blues', fmt = "d")
    plt.title(filename_calc+'\nAccuracy' + str(accuracy) +"/FPrate"+str(abnRate), fontname="MS Gothic")
    plt.xlabel('answer')
    plt.ylabel('predict')
    plt.savefig(resultpath +'/Matrics/'+filename_calc+"_heatmap.png")
    plt.close()
    print("accuracy:"+ str(accuracy))