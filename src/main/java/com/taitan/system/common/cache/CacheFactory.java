package com.taitan.system.common.cache;


import java.util.concurrent.TimeUnit;

/**
 * 缓存工厂
 */
public class CacheFactory {

    public static <K,V> LocalCache<K,V> build(Long duration, TimeUnit timeUnit) {
        return new CaffeineCache<>(duration, timeUnit);
    }

}
