import openai  
  
openai.api_key = "openai的接口apikey"   
  
completion = openai.ChatCompletion.create(  
  model="gpt-3.5-turbo",   
  messages=[{"role": "user", "content": "北国风光，千里冰封，万里雪飘，请接着续写，使用沁园春的词牌"}]  
)  
  
print(completion["choices"][0]["message"]["content"])