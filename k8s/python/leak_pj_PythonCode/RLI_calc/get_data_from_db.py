import numpy as np
import pandas as pd
import pymysql


db1 = pymysql.connect(host='localhost',
                      user='root',
                      password='123456',
                      db='python',
                      port=3306)


def get_data_from_DB(type_id, unit_id, extracttime, rt_cols='', tl_cols=''):
    # db1无视掉，因为存储容量不够所以本地搭建了两个库
    sql_out_tl = """
            SELECT {cols}
            FROM repunit_tl_{type_id}
            WHERE UNIT_ID = {unit_id}
            
    """.format(cols=tl_cols,
               type_id=type_id,
               unit_id=unit_id,
               extracttime=extracttime)

    sql_out_rt = """
            SELECT {cols}
            FROM repunit_rt_{type_id}
            WHERE UNIT_ID = {unit_id}
            AND CTMODE in (4,19)
           
    """.format(cols=rt_cols,
               type_id=type_id,
               unit_id=unit_id,
               extracttime=extracttime)

    # 抽取室外机rt数据并保存
    if rt_cols == '':
        data_rt = ''  # 置空
    else:
        data_rt = pd.read_sql(sql_out_rt.format(type_id=str(
            int(type_id)), unit_id=str(int(unit_id))), db1)
        # print(sql_out_rt.format(type_id = str(int(type_id)),unit_id = str(int(unit_id))))
        data_rt.drop_duplicates(inplace=True)

    # 抽取室外机数据并保存
    if tl_cols == '':
        data_tl = ''  # 置空
    else:
        data_tl = pd.read_sql(sql_out_tl.format(type_id=str(
            int(type_id)), unit_id=str(int(unit_id))), db1)
        # print(sql_out_tl.format(type_id = str(int(type_id)),unit_id = str(int(unit_id))))
        data_tl.drop_duplicates(inplace=True)

    return data_rt, data_tl


def get_unit_from_DB(type_id):
    data_unit = pd.read_sql(
        "SELECT DISTINCT UNIT_ID FROM repunit_rt_"+type_id, db1)
    return data_unit
