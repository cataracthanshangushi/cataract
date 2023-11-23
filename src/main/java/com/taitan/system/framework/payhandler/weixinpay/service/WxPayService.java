package com.taitan.system.framework.payhandler.weixinpay.service;

import jakarta.servlet.http.HttpServletRequest;

public interface WxPayService {
    void wxPayNotify(HttpServletRequest request);

    void wxPayCreat();
}
