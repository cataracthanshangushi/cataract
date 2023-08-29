# -*- coding: utf-8 -*-

from decimal import  * 
import pymysql
def convert_digital_decimal(value):
    # 判断decimal类型
    if type(value) is type(Decimal.from_float(0.0)):
        # 转换
        return float(value.quantize(Decimal('0.00000000000')))
    else:
        return value

def convert_digital(func):
    def _number(*args, **kwargs):
        # func：此处适用用函数，类内方法参数加 self
        results =func(*args, **kwargs)
        for data in results:
            for key,value in data.items(): 
                data[key]=convert_digital_decimal(value)
        return results
    return _number   

@convert_digital
def selectSql(sql):
    conn = pymysql.connect(host='localhost',   # 本地数据库
                        port=3306,
                        user='root',
                        password='123456',
                        db='python',
                        charset='utf8') #服务器名,账户,密码，数据库名称
    cursor = conn.cursor(cursor=pymysql.cursors.DictCursor)
    cursor.execute(sql)
    result = cursor.fetchall()
    cursor.close()     
    conn.close() 
    
    return result   


def insertSql(sql,data):
    conn = pymysql.connect(host='localhost',   # 本地数据库  
                        port=3306,
                        user='root',
                        password='123456',
                        db='python',
                        charset='utf8') #服务器名,账户,密码，数据库名称
    cursor = conn.cursor()
    cursor.executemany(sql,data)
    conn.commit()
    cursor.close()
    conn.close()   

    