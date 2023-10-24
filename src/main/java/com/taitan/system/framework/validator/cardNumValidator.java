package com.taitan.system.framework.validator;

import cn.hutool.core.util.IdcardUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class cardNumValidator implements ConstraintValidator<Wildcard, String> {
    @Override						//初始化传入的是注解
    public void initialize(Wildcard constraintAnnotation) {

    }
    //进行校验的逻辑判断
    @Override
    public boolean isValid(String cardNum, ConstraintValidatorContext constraintValidatorContext) {
        if(IdcardUtil.isValidCard(cardNum)){
            return true;
        }
        return false;
    }
}


