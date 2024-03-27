package com.taitan.system.converter;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;

public class CommonBean implements Serializable {
    /**
     * @apiNote 全局类型转换方法：入参和返参均支持泛型
     * @param target
     * @return 目标类型
     * @param <T>
     */
    public <T> T copyProperties(Class<T> target) {
        return JSON.parseObject(JSON.toJSONString(this), target);
    }
}

