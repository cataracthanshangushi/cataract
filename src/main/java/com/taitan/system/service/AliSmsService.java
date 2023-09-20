package com.taitan.system.service;

public interface AliSmsService {

    Boolean sendCode(String phone);

    Boolean checkCode(String phone, String code);
}
