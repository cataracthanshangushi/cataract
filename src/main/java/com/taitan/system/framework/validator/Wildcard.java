package com.taitan.system.framework.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {cardNumValidator.class})
public @interface Wildcard {
    //如果出错，返回的数据
    String message() default "输入的身份证格式错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

