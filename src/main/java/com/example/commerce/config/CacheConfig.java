package com.example.commerce.config;

import com.example.commerce.aspects.PerformanceMonitoringAspect;
import com.example.commerce.cache.MonitoredCacheManager;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private final PerformanceMonitoringAspect performanceMonitor;

    @Bean
    public CacheManager cacheManager() {
        // Use in-memory cache - no serialization needed
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                // Product caches
                "allProducts",
                "allProductsList",
                "productById",
                "productByName",
                // Category caches
                "categoryById",
                // Inventory caches
                "inventoryById",
                "inventoryByProductId",
                // Order caches
                "orderById",
                // User caches
                "userById",
                "userByEmail",
                // Cart caches
                "cartByUserId"
        );
        
        // Wrap with monitoring
        return new MonitoredCacheManager(cacheManager, performanceMonitor);
    }
}
