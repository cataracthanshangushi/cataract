package com.taitan.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.taitan.system.framework.easycaptcha.config.EasyCaptchaConfig;
import com.taitan.system.service.AliSmsService;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "alicloud.sms")
@Slf4j
public class AliSmsServiceImpl implements AliSmsService {

    private final RedisTemplate redisTemplate;

    private final EasyCaptchaConfig easyCaptchaConfig;

    @Setter
    private String accessKeyId;

    @Setter
    private String accessKeySecret;

    @Setter
    private String signName;

    @Setter
    private String templateCode;

    @Setter
    private String endpoint;


    @Override
    public Boolean sendCode(String phone) {
        Client client = this.createClient(accessKeyId, accessKeySecret);
        String code = RandomUtil.randomNumbers(4);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setPhoneNumbers(phone)
//                .setTemplateParam(code);
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (Exception error) {
            // 如有需要，请打印 error
            throw new RuntimeException(error);
        }
        redisTemplate.opsForValue().set(phone, code,
                easyCaptchaConfig.getTtl(), TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Boolean checkCode(String phone, String code) {
        Object cacheVerifyCode = redisTemplate.opsForValue().get(phone);
        return StrUtil.equals(code, Convert.toStr(cacheVerifyCode)) ? true : false;
    }

    public Client createClient(String accessKeyId, String accessKeySecret) {
        try {
            Config config = new Config()
                    // 必填，您的 AccessKey ID
                    .setAccessKeyId(accessKeyId)
                    // 必填，您的 AccessKey Secret
                    .setAccessKeySecret(accessKeySecret);
            // 访问的域名
            config.endpoint = endpoint;
            return new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
