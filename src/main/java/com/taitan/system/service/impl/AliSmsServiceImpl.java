package com.taitan.system.service.impl;

import com.taitan.system.service.AliSmsService;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alicloud.sms")
@Slf4j
public class AliSmsServiceImpl implements AliSmsService {
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
    public Boolean sendCode(String phone, String code) {
        Client client = this.createClient(accessKeyId, accessKeySecret);
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
            System.out.println("短信内容================" + sendSmsResponse.getBody().getMessage());
            System.out.println("短信内容================" + sendSmsResponse.getBody().getCode());
        } catch (Exception error) {
            // 如有需要，请打印 error
            throw new RuntimeException(error);
        }
        return true;
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
