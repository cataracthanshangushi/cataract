package com.taitan.system.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class  CaffeineBuilder {

    /**
     * 构建所要缓存的key cache
     */
    public static <K,V> Cache<K,V> build(int minSize, int maxSize, Long expireSeconds, TimeUnit timeUnit) {
        return Caffeine.newBuilder()
                .initialCapacity(minSize)//初始大小
                .maximumSize(maxSize)//最大数量
                .expireAfterWrite(expireSeconds, timeUnit)//过期时间
                .build();
    }

    public static <K,V> Cache<K,V> cache(Long expireSeconds, TimeUnit timeUnit) {
        return build(1,10000,expireSeconds,timeUnit);
    }
}
