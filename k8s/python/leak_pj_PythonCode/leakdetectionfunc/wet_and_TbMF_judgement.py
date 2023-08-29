import warnings

warnings.filterwarnings('ignore')

import numpy as np
import pandas as pd

class classify():
    """
    ΔRLI低下が「漏洩」、「湿り運転」、「Tb故障」いずれに由来するものかを判定する
    判定条件：
        ・湿り運転
            1. 冷房運転 ＆ 圧縮機吸入SH＜(閾値) ＆ 吐出SH＜(閾値)
            2. ΔRLI低下検出時直前x点で(y*100)%以上条件1が成立

        ・Tb故障
            1. Tb – Tc > (閾値) が成立
            2. ΔRLI低下検出時直前x点で(y*100)%以上条件1が成立
    """
    def __init__(self,opeMode,modelcode):
        """
        ハイパーパラメータの設定
        :param testpath: フォルダパス
        :param data_name: 検証データのファイル名
        :param machine_code: 機種コード
        :param opeMode: 冷房 or 暖房("cool" or "heat")
        """

        # 冷房 or 暖房
        self.opeMode = opeMode
        self.modelcode = modelcode


    def possibility_wet(self, df_dRLI_after, inhalationSH_thre=5, vomitSH_thre=20):
        """
        運転パラメータから湿り判定条件1を判定するサブルーチン
        条件1：吸入SH 及び 吐出SH がそれぞれ閾値以下になった場合に判定
        :param df_dRLI_after : 運転データ
        :param inhalationSH_thre : 吸入SHの閾値
        :param vomitSH_thre : 吐出SHの閾値
        :return: df_dRLI_after : 湿り判定条件1のカラムを追加した運転データ
        """
        df_dRLI_after["judge_wet"] = 0
        # SH < inhalationSH_thre かつ DSH < vomitSH_thre の場合 1
        df_dRLI_after.loc[df_dRLI_after[df_dRLI_after['SHsuc'] < inhalationSH_thre]
        [df_dRLI_after["DSH"] < vomitSH_thre].index, "judge_wet"] = 1

        return df_dRLI_after

    def possibility_Tb(self, df_dRLI_after, Tb_error_thre=0):
        """
        運転パラメータからTb故障判定条件1を判定するサブルーチン
        条件1：Tb - 外1Tc が閾値を超えた場合に判定
        :param Tb_error_thre: 吸入SHの閾値
        :return: df_dRLI_after : 湿り判定条件1のカラムを追加した運転データ
        """
        df_dRLI_after["judge_Tb"] = 0

        # Tb - 外1Tc の場合 1
        if self.opeMode == '3':
            df_dRLI_after.loc[df_dRLI_after[df_dRLI_after["外1Thex出"] - df_dRLI_after['OU1Tc'] > Tb_error_thre].index, "judge_Tb"] = 1
        elif (self.opeMode == '1') or (self.opeMode == '2'):
            df_dRLI_after.loc[df_dRLI_after[df_dRLI_after['OU1ThexLiq'] - df_dRLI_after['OU1Tc'] > Tb_error_thre].index, "judge_Tb"] = 1

        return df_dRLI_after

    def judgement_leak_wet_Tb(self, df_dRLI_after, leak_label, wet_decision_days=10,
                                      wet_num_thre=0.7, Tb_decision_days=10, Tb_num_thre=0.7):
        """
        運転パラメータから湿り判定条件2、Tb故障判定条件2を判定し、最終判定を下すサブルーチン
        条件2：ΔRLI低下検出時直前x点で(y*100)%以上条件1が成立
        :param df_dRLI_after: 運転データ
        :param leak: deltaRLI_decrase_judgement.py の ΔRLI低下判定（index=日報日時）
        :param wet_decision_days : 湿り判定のx
        :param wet_num_thre : 湿り判定のy
        :param Tb_decision_days : Tb故障判定のx
        :param Tb_num_thre : Tb故障判定のy
        :return: df_dRLI_after : 判定ラベルのカラムを追加した運転データ
        """
        df_dRLI_after["dRLI_decrease"] = leak_label
        df_dRLI_after.loc[df_dRLI_after.index[df_dRLI_after["dRLI_decrease"] != 1], "dRLI_decrease"] = 0 #ここは問題　NaNも０にしてしまう
        df_dRLI_after["predict"] = df_dRLI_after["dRLI_decrease"]

        for i in range(wet_decision_days, len(df_dRLI_after)):
            if np.sum(df_dRLI_after.iloc[i - wet_decision_days:i, -4]) / wet_decision_days >= wet_num_thre \
                    and df_dRLI_after.iloc[i, -2] == 1:
                df_dRLI_after.iloc[i, -1] = 2

        for i in range(Tb_decision_days, len(df_dRLI_after)):
            if np.sum(df_dRLI_after.iloc[i - Tb_decision_days:i, -3]) / Tb_decision_days >= Tb_num_thre \
                    and df_dRLI_after.iloc[i, -2] == 1:
                df_dRLI_after.iloc[i, -1] = 3
        return df_dRLI_after
    

    def get_error_frequency_span(self, df_dRLI_after):
        """
        データ内に故障があるかないかを判定し、ある場合その期間を取得する
        :param df_dRLI_after : ld.run(df)済のデータフレーム
        :return detect_label : 故障があるかないか 0:故障無し 1:故障あり
        :return pre_span : 故障期間 複数ある場合でも1つのstring変数にまとめて返す
        :return error_count : 各故障の判定数
        """
        # 0,99以外の定義されている判定ラベルを記入
        label = [1,2,3]

        # 全ラベルをカウントするために記述
        LabelAll = [0, 1, 2, 3, 99]

        # 各故障の出現回数をカウントするための準備
        error_count = np.zeros(len(label))
        LabelNum = np.zeros(len(LabelAll))

        # 冷媒漏れと判断された日数のカウント
        if np.sum(df_dRLI_after["predict"]) == 0:
            # 故障日数が0の場合判定ラベルを0（正常）にする
            detect_label = 0
            LabelNum[0] = len(df_dRLI_after)
            pre_span = "-"
        else:
            # 各故障の出現回数をカウント
            for i, lab in enumerate(label):
                error_count[i] = len(np.where(df_dRLI_after["predict"]==lab)[0])

            # 全ラベルのの出現回数をカウント
            for i, lab in enumerate(LabelAll):
                LabelNum[i] = len(np.where(df_dRLI_after["predict"] == lab)[0])

            # 0か99以外の判定ラベルが存在する場合
            if np.max(error_count) != 0:
                # 出現数最大の故障を特定
                # max_lab = label[np.argmax(error_count)]
                # 出現数が最大の故障を判別結果として返す
                detect_label = 1
                if error_count[1] >= 5:
                    detect_label = 2
                if error_count[2] >= 5:
                    detect_label = 3

                #######故障開始と終了の日を取得する######
                pred = df_dRLI_after["predict"].copy()
                pred[pred == 99] = 0  # その他判定は無視
                pred[pred > 0] = 1  # 故障判定のラベルを全て1に
                pred[pred >= 1] = 1  # 移動平均後一定の閾値で評価するため

                # 幅5か月で移動平均を取って歯抜けデータを矯正する
                pred = pred.rolling(window=5, center=True, min_periods=3).mean()

                # 閾値を0.6としてそれ以上のみを故障期間とする（前後5か月の内3か月以上故障判定がある場合）
                pred[pred >= 0.6] = 1
                pred[pred < 0.6] = 0

                # ラベル切り替わり点の取得
                switch_point = np.array([])
                plus_or_minus = np.array([])  # -1:故障→正常　or 1:正常→故障
                for i in range(1, len(pred)):
                    diff = pred[i] - pred[i - 1]
                    if diff != 0:
                        switch_point = np.append(switch_point, i)
                        plus_or_minus = np.append(plus_or_minus, diff)

                # 切り替わり点の日時を取得
                # 1つの列に故障期間が複数ある場合でもすべて入れる
                pre_span = ""
                write_flag = 1
                for i in range(len(switch_point)):
                    if plus_or_minus[i] == 1:
                        # 故障開始
                        if write_flag == 1:
                            pre_span = pre_span + str(df_dRLI_after.index[int(switch_point[i])].year) \
                                       + "年" + str(df_dRLI_after.index[int(switch_point[i])].month) + "月～"
                    elif plus_or_minus[i] == -1:
                        if i == 0:
                            # データが故障から始まっている場合
                            pre_span = "～" + pre_span
                        # 故障終了
                        if i != len(switch_point)-1 and df_dRLI_after.index[int(switch_point[i + 1])].year \
                                == df_dRLI_after.index[int(switch_point[i])].year \
                                and df_dRLI_after.index[int(switch_point[i + 1])].month \
                                - df_dRLI_after.index[int(switch_point[i])].month <= 1:
                            # 故障期間が同月にある場合 or 連続した月にある場合 は記載せず、期間をつなげる
                            write_flag = 0
                        elif i != len(switch_point)-1 and df_dRLI_after.index[int(switch_point[i + 1])].year \
                                - df_dRLI_after.index[int(switch_point[i])].year == 1 \
                                and df_dRLI_after.index[int(switch_point[i + 1])].month \
                                - df_dRLI_after.index[int(switch_point[i])].month == -11:
                            # 上と同様だが期間が年をまたいで連続で存在する場合
                            write_flag = 0
                        else:
                            write_flag = 1

                        if write_flag == 1:
                            pre_span = pre_span + str(df_dRLI_after.index[int(switch_point[i] - 1)].year) \
                                       + "年" + str(df_dRLI_after.index[int(switch_point[i] - 1)].month) + "月 / "

            else:
                # その他判定ラベルしかない場合
                detect_label = 0
                pre_span = "-"

        return detect_label, pre_span, error_count, LabelNum

    
    def run(self, leak_label, df_dRLI_after):
        """
        出力処理の実行

        :param  args : [0:作業フォルダ名, 1:データ名(~cool.xlsx), 2:冷房or暖房("cool"or"heat")]
        :param  leak : delta_RLI_decrease_judgment.pyの結果データフレームカラム　　　　　　df_dRLI_after["label"]
        :return:
        """
        #df（df_dRLI_afterのこと）と暖房or冷房の情報
        # cl = classify(df_rli,opeMode,modelcode)
        df_dRLI_after = df_dRLI_after.set_index('Datetime')
        # df_dRLI_after = df_dRLI_after.resample("D").mean()
        df_dRLI_after.loc[:,'DSH'] = df_dRLI_after.loc[:,'OU1Td']-df_dRLI_after.loc[:,'OU1Tc'] 
    
        df_dRLI_after = self.possibility_wet(df_dRLI_after)
        df_dRLI_after = self.possibility_Tb(df_dRLI_after)
        df_dRLI_after = self.judgement_leak_wet_Tb(df_dRLI_after, leak_label)
        detect_label, pre_span, error_count, LabelNum = self.get_error_frequency_span(df_dRLI_after)

        # return  detect_label,pre_span,error_count,LabelNum

        # 判定ラベルを日本語に変換
        if detect_label == 0:
            detect_label = "normal"
        elif detect_label == 1:
            detect_label = "leak"
        elif detect_label == 2:
            detect_label = "wet"
        elif detect_label == 3:
            detect_label = "Tb fault"

        return  detect_label,pre_span