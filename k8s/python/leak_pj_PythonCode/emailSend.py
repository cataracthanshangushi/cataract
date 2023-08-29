import os
import configparser
import pymysql
import smtplib
from email.mime.text import MIMEText
from email.header import Header

# conn = pymysql.connect(host='localhost',   # 本地数据库
#                         port=3306,
#                         user='root',
#                         password='123456',
#                         db='python',
#                         charset='utf8') #服务器名,账户,密码，数据库名称
# cursor = conn.cursor(cursor=pymysql.cursors.DictCursor)
# cursor.execute("select max(creat_Dt) as t from judgementresult")
# result = cursor.fetchall()
# cursor.close()     
# conn.close() 

conf = configparser.ConfigParser()
conf.read(r'C:\Users\pb080086\Desktop\config.conf',encoding = 'utf-8')

url_baidu = conf.get('parameter', 'url_baidu')
url_taobao = conf.get('parameter', 'url_taobao')
email_baidu = conf.get('email', 'emailServerUrl')
email_taobao = conf.get('email', 'emailServerPort')
    
email= conf.get('email', 'CustomerServiceCenter').split(',')

# 创建 SMTP 对象
# smtp = smtplib.SMTP()
# # 连接（connect）指定服务器
# smtp.connect("smtp.126.com", port=25)
# # 登录，需要：登录邮箱和授权码
# smtp.login(user="fairy135158@126.com", password="UUSCLRAZVPSYGVNA")



# sender = 'from@runoob.com'
# receivers = ['429240967@qq.com']  # 接收邮件，可设置为你的QQ邮箱或者其他邮箱
 
# message = MIMEText('Python 邮件发送测试...', 'plain', 'utf-8')
# message['From'] = Header("冷媒检知自动邮件", 'utf-8')
# message['To'] =  Header("郑明鹏", 'utf-8')
 
# subject = 'Python SMTP 邮件测试'
# message['Subject'] = Header(subject, 'utf-8')
 
 

# smtpObj = smtplib.SMTP() 
# smtpObj.connect("smtp.126.com", port=25)    # 25 为 SMTP 端口号
# smtpObj.login("smtp.126.com","smtp.126.com")  
# smtpObj.sendmail(sender, receivers, message.as_string())
  

fname = r'C:\Users\pb080086\Desktop\leakCheck.txt'
with open(fname, 'rb') as f:  #打开文件
    off = -50      #设置偏移量
    while True:
        f.seek(off, 2) #seek(off, 2)表示文件指针：从文件末尾(2)开始向前50个字符(-50)
        lines = f.readlines() #读取文件指针范围内所有行
        if len(lines)>=2: #判断是否最后至少有两行，这样保证了最后一行是完整的
            last_line = lines[-1] #取最后一行
            break
        #如果off为50时得到的readlines只有一行内容，那么不能保证最后一行是完整的
        #所以off翻倍重新运行，直到readlines不止一行
        off *= 2

   
    
# last_line=str(last_line)

last_line = str(last_line)
print (last_line[2:12])

print (str(last_line[2:12])=='2022-12-07')
# print (url_baidu)
# print (url_taobao)
# print (email_baidu)
# print (email_taobao)
# print (email[1])
# print (len(email))
# print (result[0]['t']) 