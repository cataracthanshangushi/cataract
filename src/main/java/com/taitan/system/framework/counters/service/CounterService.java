package com.taitan.system.framework.counters.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CounterService {
    private Map<String, AtomicInteger> counters = new HashMap<>();

    public int increment(String key) {
        AtomicInteger counter = counters.computeIfAbsent(key, k -> new AtomicInteger());
        return counter.incrementAndGet();
    }

    public int getCount(String key) {
        AtomicInteger counter = counters.get(key);
        return counter != null ? counter.get() : 0;
    }
}
