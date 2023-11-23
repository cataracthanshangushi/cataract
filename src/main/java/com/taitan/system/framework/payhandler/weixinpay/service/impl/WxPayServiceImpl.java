package com.taitan.system.framework.payhandler.weixinpay.service.impl;


import cn.hutool.core.util.StrUtil;
import com.taitan.system.common.util.HttpClientUtils;
import com.taitan.system.framework.payhandler.weixinpay.config.WxPayConfig;
import com.taitan.system.framework.payhandler.weixinpay.service.WxPayService;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.nativepay.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.wechat.pay.java.core.http.Constant;

@Service
@RequiredArgsConstructor
public class WxPayServiceImpl implements WxPayService {

    private final WxPayConfig wxPayConfig;

    @Override
    public void wxPayNotify(HttpServletRequest request) {
        String notifyData = HttpClientUtils.getRequestParam(request).toString();
        String timestamp = request.getHeader(Constant.WECHAT_PAY_TIMESTAMP);
        String nonce = request.getHeader(Constant.WECHAT_PAY_NONCE);
        String signType = request.getHeader("Wechatpay-Signature-Type");
        String serialNo = request.getHeader(Constant.WECHAT_PAY_SERIAL);
        String signature = request.getHeader(Constant.WECHAT_PAY_SIGNATURE);

        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(wxPayConfig.getMchId())
                .privateKeyFromPath(wxPayConfig.getKeyPath())
                .merchantSerialNumber(wxPayConfig.getMchSerialNo())
                .apiV3Key(wxPayConfig.getApiKey())
                .build();

        NotificationParser parser = new NotificationParser(config);
        RequestParam requestParam=new RequestParam.Builder()
                .serialNumber(serialNo)
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                // 若未设置signType，默认值为 WECHATPAY2-SHA256-RSA2048
                .signType(signType)
                .body(notifyData)
                .build();
        Transaction parse = parser.parse(requestParam, Transaction.class);
        if(!StrUtil.equals("SUCCESS", parse.getTradeState().toString())){}
    }

    @Override
    public void wxPayCreat() {

    }
}
