package com.example.commerce.cache;

import com.example.commerce.aspects.PerformanceMonitoringAspect;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
public class MonitoredCacheManager implements CacheManager {

    private final CacheManager delegate;
    private final PerformanceMonitoringAspect performanceMonitor;

    @PostConstruct
    public void init() {
        log.info("🚀 Cache monitoring is active - tracking hits/misses via AOP");
        log.info("📊 View metrics at: GET /api/performance/cache-metrics");
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = delegate.getCache(name);
        return cache != null ? new MonitoredCache(cache, performanceMonitor) : null;
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
