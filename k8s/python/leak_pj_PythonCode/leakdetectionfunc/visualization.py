import numpy as np
import pandas as pd
import matplotlib
# matplotlib.use('Agg')
from matplotlib import pyplot as plt
import os
from os import path
from PIL import Image, ImageDraw, ImageFont
import re
import datetime
import sys
import glob
import tkinter.filedialog




#    visualising.visualize(detect_label,pre_span,df_reset,df,opeMode,file_name,df_after)
class visualize:
    def __init__(self, resultpath, detect_label, pre_span, df_rli, opeMode, buildname, lc_number, draw_from=0, draw_to=3000, draw_range=30,
                 ts1="dRLI_RFR", ts2='OU1EV2pls', ts3="DSH", ts4='SHsuc'):
        """
        dRLI_RFR vs 外気温 の月報グラフ、dRLI_RFR・EV2・DSH・SHの時系列グラフを描画し、
        ツールの判定結果毎にフォルダ分けする
        インスタンスを作成した段階で実行されるように作っています

        ☆★☆入力が必要な引数★☆★
        :param detect_label   : 判定結果(0:正常,1:漏洩,2:湿り,3:Tb故障)
        :param pre_span  : 故障期間(文字列データ)
        :param df        : ΔRLI低下判定から呼び出した場合,*coolRLI.xlsxのデータ、
                           故障分類から呼び出した場合,*cool.xlsxのデータ
        :param logic     : ΔRLI低下判定(0)か故障分類(1)かどちらから呼び出したか
        :param buildpath : *coolRLI.xlsx と *cool.xlsx の入っているフォルダ(対象フォルダ)のpath
        :param buildname : *coolRLI.xlsx や *cool.xlsx の 「*」に当たる部分の文字列


        ☆★☆任意入力な引数★☆★

        描画する範囲（年）の絞り込み
        描画範囲であってツール判定範囲ではないので、指定範囲によっては図と判定結果が対応しない可能性があります
        :param draw_from : 前範囲を指定 (絶対範囲)
        :param draw_to   : 後範囲を指定 (絶対範囲)
        :param draw_range: 最新年までの表示する年数を指定 (相対範囲)

        時系列グラフを描写するデータのカラム名
        ここの指定を変更することで描画するデータを変更できる（外から呼び出すときに指定するのでも可）
        :param ts1       : 上から1つめ
        :param ts2       : 上から2つめ
        :param ts3       : 上から3つめ
        :param ts4       : 上から4つめ
        """
        self.resultpath = resultpath
        self.detect_label = detect_label
        self.pre_span = pre_span
        self.buildname = buildname
        self.opeMode = opeMode
        self.lc_number = lc_number

        self.ts1 = ts1
        self.ts2 = ts2
        self.ts3 = ts3
        self.ts4 = ts4
        
        # df_rli['Datetime'] = pd.to_datetime(df_rli['Datetime'])

        # PNGdataフォルダが存在しない場合に作成・・・(1)
        try:
            os.makedirs(self.resultpath+'/PNGdata', exist_ok=True)
        except FileExistsError:
            pass

        # (1)の中に実行結果をまとめるフォルダを作成・・・(2)
        # (同じ日・同じ対象フォルダで実行した場合は中身が更新されます)
        try:
           # 時間取得
           time = datetime.datetime.now().strftime('%Y-%m-%d_')
           # 対象フォルダ名取得
           self.subdirname = time + buildname
           # フォルダ作成
           # os.mkdir("PNGdata\\" + self.subdirname)
        except FileExistsError:
           pass

        # 判定ラベルを定義
        """☆☆☆書き換えポイント☆☆☆"""
        # 判定ラベルが増えた場合はelifを追加してください
        self.flag = 0
        if self.detect_label == "normal":
            self.fill = (0, 255, 0)  # 緑色
        elif self.detect_label == "leak":
            self.fill = (255, 0, 0)  # 赤色
        elif self.detect_label == "wet":
            self.fill = (0, 0, 255)  # 青色
        elif self.detect_label == "Tb fault":
            self.fill = (0, 0, 0)  # 黒色
        ########################
        # elif self.detect_label == ?:
        #    detect_label = "ラベル名"
        #    fill = (?, ?, ?)
        ########################
        else:
            self.flag = 1

        try:
            # （2）の中に判定結果毎にフォルダを作成・・・(3)
            # (対象フォルダ内の全データの中で判定されない故障ラベルについてのフォルダは作成されない)
            self.target_dir = self.resultpath+"/PNGdata/" + self.detect_label 
            os.makedirs(self.target_dir, exist_ok=True)
        except FileExistsError:
            pass

        # *coolRLI.xlsx と *cool.xlsx それぞれのデータを整える

        self.df_rli = df_rli.set_index('Datetime')

        # 描画範囲に絞り込み
        self.df_rli = self.df_rli[(self.df_rli.index.year >= draw_from) & (self.df_rli.index.year <= draw_to)]

        # 最新年から選択した年数分に絞る
        self.df_rli = self.df_rli[self.df_rli.index.year > self.df_rli.index.year[-1] - draw_range]

        # 出力するデータ列を抽出
        # 左側 dRLI_RFR vs 外気温 月報グラフ


        self.RLI_mean = self.df_rli["dRLI_RFR"].resample("M").mean().dropna()
        self.Ta = self.df_rli['OU1Ta'].resample("M").mean().dropna()
        # 右側時系列グラフ
        self.col1 = self.df_rli[ts1].dropna()
        self.col2 = self.df_rli[ts2].dropna()
        self.col3 = self.df_rli[ts3].dropna()
        self.col4 = self.df_rli[ts4].dropna()

        # 処理の実行
        self.__get_ML_span()
        self.visualize_by_year()
        self.drawing()
        #self.to_list()

    def visualize_by_year(self):
        """
        年毎に色分けして図を描画するメソッド
        :return:
        """
        # dRLI_RFR vs 外気温 の月報グラフのラベルマーカー定義
        markers = ["s", "o", "v", "^", "p", "*", "D", "8", "x", "+"]
        # 存在する年のリストを取得
        year_list = self.col1.index.year.unique().tolist()

        # 以下描画
        plt.figure(figsize=(20, 10))

        for i in range(6):
            if i == 0:
                # dRLI_RFR vs 外気温 の月報グラフ
                df_rli = self.RLI_mean
                title = "dRLI_RFR"
                # 描画する相対位置・図の相対大きさを指定
                plt.axes([0.05, 0.35, 0.3, 0.6])
            elif i == 1:
                # dRLI_RFRの時系列グラフ
                df_rli = self.col1
                title = "dRLI"
                plt.axes([0.4, 0.825, 0.55, 0.14])
            elif i == 2:
                # dRLI_RFRの時系列グラフ
                df_rli = self.df_rli["dRLI_RFR_after"]
                title = "dRLI(Correction)"
                plt.axes([0.4, 0.625, 0.55, 0.14])
            elif i == 3:
                # EV2の時系列グラフ
                df_rli = self.col2
                title = "EV2"
                plt.axes([0.4, 0.425, 0.55, 0.14])
            elif i == 4:
                # DSHの時系列グラフ
                df_rli = self.col3
                title = "DSH"
                plt.axes([0.4, 0.225, 0.55, 0.14])
            else:
                # SHの時系列グラフ
                df_rli = self.col4
                title = "SH"
                plt.axes([0.4, 0.025, 0.55, 0.14])

            plt.grid(True)
            for j, year in enumerate(year_list):
                dfy = df_rli[df_rli.index.year == year]

                if i == 0:
                    # dRLI_RFR vs 外気温 の月報グラフの描画
                    Tay = self.Ta[self.Ta.index.year == year]
                    if len(str(j)) == 2:
                        # matplotlibの色が指定なしだと10色しかないようで、マーカーも10個で順番に回すと
                        # 10年後の色・マーカーが一致してしまうので、ずらすための処理
                        plt.plot(Tay, dfy, marker=markers[j % (10 - int(str(j)[0]))], label=str(year), linewidth=1)
                    else:
                        plt.plot(Tay, dfy, marker=markers[j], label=str(year), linewidth=1)
                    # 温度の描画値域を指定
                    plt.xlim(5, 40)
                else:
                    # 時系列グラフの描画
                    plt.plot(dfy.index, dfy, label=str(year), linewidth=1)
                plt.title(title, fontsize=18)

            if i != 0:
                # 時系列グラフに月平均を描画
                df_mean = df_rli.resample("M").mean().dropna()
                plt.plot(df_mean.index, df_mean, label=None, linewidth=1, color="black")

            if i == 1:
                # 最上段の時系列グラフの最上段
                xlim = plt.xlim()

            if i != 0:
                # 時系列グラフにΔRLI低下判定期間を塗りつぶし
                ylim = plt.ylim()
                for k in range(len(self.df_span)):
                    plt.axvspan(self.df_span.iloc[k,0], self.df_span.iloc[k,1], facecolor='blue', alpha=0.1)
                plt.xlim(xlim)
                plt.ylim(ylim)

            if i == 0:
                # 凡例を描画
                plt.legend(loc='upper left',
                           bbox_to_anchor=(0.0, -0.2, 0.7, 0.1),
                           borderaxespad=0.,
                           ncol=4,
                           fontsize=15,
                           labelspacing=1,
                           shadow=True)
        plt.savefig(self.target_dir + "\\"+ self.lc_number + ".png")
        # plt.savefig(self.target_dir + "\\{}.png".format(self.buildname))
        # plt.savefig("PNGdata\\{}.png".format(self.buildname))
        # plt.savefig("static\\assets\\img\\{}.png".format(self.buildname))
        plt.close()

    def drawing(self):
        """
        データ名(*の部分)・ツールの判定結果・故障判定期間を出力画像に記述する
        :return:
        """
        img = Image.open(self.target_dir + "\\"+ self.lc_number + ".png")
        # img = Image.open(self.target_dir + "\\{}.png".format(self.buildname))
        # img = Image.open("PNGdata\\{}.png".format(self.buildname))
        # img = Image.open("static\\assets\\img\\{}.png".format(self.buildname))
        draw = ImageDraw.Draw(img)
        # フォント指定（環境によってはここでエラーになる可能性大!!）
        font = ImageFont.truetype("C:\Windows\Fonts\simsunb.ttf", 27)
        font2 = ImageFont.truetype("C:\Windows\Fonts\simsunb.ttf", 22)
        font3 = ImageFont.truetype("C:\Windows\Fonts\simsunb.ttf", 17)
        draw.text((20, 850), self.buildname, fill=(0, 0, 0), font=font)

        # ツールから呼び出して判定結果が存在する場合はそれを記述
        if self.flag == 0:
            draw.text((560, 850), "judge:", fill=(0, 0, 0), font=font)
            draw.text((650, 850), self.detect_label, fill=self.fill, font=font)

            # 故障判定期間を記述
            # 文字が右にはみ出ないように期間数（"/"の数で取得）で場合分け
            draw.text((20, 895), "error span:", fill=(0, 0, 0), font=font2)
            slash_point = [m.span() for m in re.finditer('/', self.pre_span)]

            if len(slash_point) <= 3:
                draw.text((30, 920), self.pre_span, fill=(0, 0, 0), font=font3)
            elif 3 < len(slash_point) <= 6:
                draw.text((30, 920), self.pre_span[:slash_point[2][1]], fill=(0, 0, 0), font=font3)
                draw.text((30, 940), self.pre_span[slash_point[2][1] + 1:], fill=(0, 0, 0), font=font3)
            else:
                draw.text((30, 920), self.pre_span[:slash_point[2][1]], fill=(0, 0, 0), font=font3)
                draw.text((30, 940), self.pre_span[slash_point[2][1] + 1: slash_point[5][1]], fill=(0, 0, 0),
                          font=font3)
                draw.text((30, 960), self.pre_span[slash_point[5][1] + 1:], fill=(0, 0, 0), font=font3)

        img.save(self.target_dir + "\\"+ self.lc_number + ".png")
        # img.save(self.target_dir + "\\{}".format(self.buildname)+ self.lc_number + ".png")
        # img.save("PNGdata\\{}.png".format(self.buildname))
        # img.save("static\\assets\\img\\{}.png".format(self.buildname))

        #ローカルに上げるための準備
        # img_PNG = Image.open("static\\assets\\img\\{}.png".format(self.buildname))
        # img_PNG.save("PNGdata\\{}.png".format(self.buildname))

        #img_PNG.save(os.path.join('./uploads',self.buildname + ".png"))


    def to_list(self):
        """
        判定フォルダごとにデータリスト(.xlsx)を作成する
        追記式で記入していくが、すでに記入されているデータは追加されない仕様にしている
        :return:
        """
        try:
            # list.xlsxの読み込み
            lis = pd.read_excel(self.target_dir + "\\list.xlsx")
            append = pd.DataFrame([self.buildname], columns=["LC_number"], index=None)
            if append.values not in np.array(lis):
                lis = pd.concat([lis, append])
        except:
            lis = pd.DataFrame([self.buildname], columns=["LC_number"], index=None)

        count = 0
        while True:
            try:
                # 付け足したデータを保存
                lis.to_excel(self.target_dir + "\\list.xlsx", index=None)
                break
            except:
                # "leak_detection_result.xlsx"が開かれている場合はエラーになるので閉じるまで待つ
                if count == 0:
                    print("フォルダ {} 内の「list.xlsx」を開いている場合は閉じてください".format(self.detect_label))
                    print("開いていないのにこのメッセージが表示されている場合はプログラムを終了してください")
                count = 1

    def __get_ML_span(self):
        """
        pre_spanから故障期間を数値で取得し、時系列データフレームに格納する
        :return:
        """
        span_list = re.findall('([0-9]*\S)', self.pre_span)
        spans = span_list.count("～")
        self.df_span = pd.DataFrame([])

        for i in range(spans):
            if span_list[0] == "～":
                if i == 0:
                    lfromY = 1900
                    lfromM = 1
                    ltoY = int(span_list[1][:-1])
                    ltoM = int(span_list[2][:-1])
                else :
                    lfromY = int(span_list[3 + 6 * (i - 1) + 1][:-1])
                    lfromM = int(span_list[3 + 6 * (i - 1) + 2][:-1])
                    if i != spans-1:
                        ltoY = int(span_list[3 + 6 * (i - 1) + 4][:-1])
                        ltoM = int(span_list[3 + 6 * (i - 1) + 5][:-1])
                    else:
                        if span_list[-1] != "～":
                            ltoY = int(span_list[3 + 6 * (i - 1) + 4][:-1])
                            ltoM = int(span_list[3 + 6 * (i - 1) + 5][:-1])
                        else:
                            ltoY = 2200
                            ltoM = 12
            else:
                lfromY = int(span_list[6 * i + 0][:-1])
                lfromM = int(span_list[6 * i + 1][:-1])
                if i != spans-1:
                    ltoY = int(span_list[6 * i + 3][:-1])
                    ltoM = int(span_list[6 * i + 4][:-1])
                else:
                    if span_list[-1] != "～":
                        ltoY = int(span_list[6 * i + 3][:-1])
                        ltoM = int(span_list[6 * i + 4][:-1])
                    else:
                        ltoY = 2200
                        ltoM = 12

            if ltoM != 12:
                ltoM += 1
            else:
                ltoY += 1
                ltoM = 1

            self.df_span = pd.concat([self.df_span,pd.DataFrame([[datetime.datetime(lfromY, lfromM,1),
                                                                 datetime.datetime(ltoY, ltoM, 1)]])], axis=0)



