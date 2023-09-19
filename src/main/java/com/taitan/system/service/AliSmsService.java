package com.taitan.system.service;

public interface AliSmsService {

    Boolean sendCode(String phone, String code);
}
