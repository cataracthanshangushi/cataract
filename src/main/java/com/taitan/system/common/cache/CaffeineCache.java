package com.taitan.system.common.cache;


import com.github.benmanes.caffeine.cache.Cache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CaffeineCache<K, V> implements LocalCache<K, V> {

    private Cache<K, V> cache;


    public CaffeineCache(Long duration, TimeUnit timeUnit) {
        this.cache = CaffeineBuilder.cache(duration, timeUnit);
    }

    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public V get(K key, V defaultValue) {
        return cache.get(key, v -> defaultValue);
    }

    @Override
    public void delete(K key) {
        Optional.ofNullable(key).ifPresent(k -> cache.invalidate(key));
    }

    @Override
    public void set(K key, V value) {
        Optional.ofNullable(key).ifPresent(k -> cache.put(k, value));
    }
}
