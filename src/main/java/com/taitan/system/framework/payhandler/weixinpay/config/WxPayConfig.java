package com.taitan.system.framework.payhandler.weixinpay.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wx")
@Data
@ToString
public class WxPayConfig {
    private String appId;

    private String mchId;

    private String mchSerialNo;

    private String appSecret;

    private String apiKey;

    private String notifyUrl;

    private String keyPath;
}
