package com.taitan.system.common.cache;

import javax.annotation.Nonnull;

public interface LocalCache<K,T> {

    T get(K key);

    T get(K key, T defaultValue);

    void delete(K key);

    void set(@Nonnull K key, @Nonnull T value);

}
