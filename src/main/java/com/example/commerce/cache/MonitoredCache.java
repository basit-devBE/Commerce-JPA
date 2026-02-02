package com.example.commerce.cache;

import com.example.commerce.aspects.PerformanceMonitoringAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class MonitoredCache implements Cache {

    private final Cache delegate;
    private final PerformanceMonitoringAspect performanceMonitor;

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        ValueWrapper value = delegate.get(key);
        String cacheKey = getName() + "::" + key;
        
        if (value != null) {
            performanceMonitor.recordCacheHit(cacheKey);
            log.debug("🎯 CACHE HIT: {}", cacheKey);
        } else {
            performanceMonitor.recordCacheMiss(cacheKey);
            log.debug("❌ CACHE MISS: {}", cacheKey);
        }
        
        return value;
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        T value = delegate.get(key, type);
        String cacheKey = getName() + "::" + key;
        
        if (value != null) {
            performanceMonitor.recordCacheHit(cacheKey);
            log.debug("🎯 CACHE HIT: {}", cacheKey);
        } else {
            performanceMonitor.recordCacheMiss(cacheKey);
            log.debug("❌ CACHE MISS: {}", cacheKey);
        }
        
        return value;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        String cacheKey = getName() + "::" + key;
        
        // Check if exists first
        ValueWrapper existing = delegate.get(key);
        if (existing != null) {
            performanceMonitor.recordCacheHit(cacheKey);
            log.debug("🎯 CACHE HIT: {}", cacheKey);
        } else {
            performanceMonitor.recordCacheMiss(cacheKey);
            log.debug("❌ CACHE MISS: {}", cacheKey);
        }
        
        return delegate.get(key, valueLoader);
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        delegate.put(key, value);
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        return delegate.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return delegate.evictIfPresent(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean invalidate() {
        return delegate.invalidate();
    }
}
