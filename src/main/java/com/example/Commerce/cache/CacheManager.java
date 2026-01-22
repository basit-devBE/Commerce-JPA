package com.example.Commerce.cache;

import com.example.Commerce.Aspects.PerformanceMonitoringAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@Slf4j
public class CacheManager {

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    private final PerformanceMonitoringAspect performanceMonitor;

    public CacheManager(PerformanceMonitoringAspect performanceMonitor) {
        this.performanceMonitor = performanceMonitor;
    }

    public <T> T get(String key, Supplier<T> supplier) {
        Object cached = cache.get(key);
        if (cached != null) {
            performanceMonitor.recordCacheHit(key);
            log.info("Cache HIT: {}", key);
            return (T) cached;
        }
        performanceMonitor.recordCacheMiss(key);
        log.info("Cache MISS: {}", key);
        return (T) cache.computeIfAbsent(key, k -> supplier.get());
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}
