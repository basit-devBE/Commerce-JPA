package com.example.Commerce.Aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {

    private final Map<String, QueryMetrics> dbMetrics = new ConcurrentHashMap<>();
    private final Map<String, CacheMetrics> cacheMetrics = new ConcurrentHashMap<>();

    @Around("execution(* com.example.Commerce.Repositories..*(..))")
    public Object monitorDatabaseFetch(ProceedingJoinPoint joinPoint) throws Throwable {
        String fullSignature = joinPoint.getSignature().getDeclaringTypeName();
        String className = fullSignature.substring(fullSignature.lastIndexOf('.') + 1);
        String methodName = joinPoint.getSignature().getName();
        String fullKey = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            dbMetrics.computeIfAbsent(fullKey, k -> new QueryMetrics())
                    .recordExecution(executionTime);
            
            log.info("DB Query: {} took {}ms", fullKey, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            dbMetrics.computeIfAbsent(fullKey + "_ERROR", k -> new QueryMetrics())
                    .recordExecution(executionTime);
            throw e;
        }
    }

    public Map<String, Map<String, Object>> getDbFetchTimes() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        dbMetrics.forEach((key, metrics) -> {
            Map<String, Object> metricData = new HashMap<>();
            metricData.put("count", metrics.getCount());
            metricData.put("totalTime", metrics.getTotalTime());
            metricData.put("avgTime", metrics.getAverageTime());
            metricData.put("minTime", metrics.getMinTime());
            metricData.put("maxTime", metrics.getMaxTime());
            metricData.put("unit", "ms");
            result.put(key, metricData);
        });
        return result;
    }

    public void clearMetrics() {
        dbMetrics.clear();
        cacheMetrics.clear();
    }

    public void recordCacheHit(String key) {
        cacheMetrics.computeIfAbsent(key, k -> new CacheMetrics()).incrementHit();
    }

    public void recordCacheMiss(String key) {
        cacheMetrics.computeIfAbsent(key, k -> new CacheMetrics()).incrementMiss();
    }

    public Map<String, Map<String, Object>> getCacheMetrics() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        cacheMetrics.forEach((key, metrics) -> {
            Map<String, Object> metricData = new HashMap<>();
            metricData.put("hits", metrics.getHits());
            metricData.put("misses", metrics.getMisses());
            metricData.put("hitRate", metrics.getHitRate());
            result.put(key, metricData);
        });
        return result;
    }

    private static class QueryMetrics {
        private final AtomicInteger count = new AtomicInteger(0);
        private long totalTime = 0;
        private long minTime = Long.MAX_VALUE;
        private long maxTime = 0;

        public synchronized void recordExecution(long time) {
            count.incrementAndGet();
            totalTime += time;
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
        }

        public int getCount() {
            return count.get();
        }

        public long getTotalTime() {
            return totalTime;
        }

        public double getAverageTime() {
            return count.get() > 0 ? (double) totalTime / count.get() : 0;
        }

        public long getMinTime() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }

        public long getMaxTime() {
            return maxTime;
        }
    }

    private static class CacheMetrics {
        private final AtomicInteger hits = new AtomicInteger(0);
        private final AtomicInteger misses = new AtomicInteger(0);

        public void incrementHit() {
            hits.incrementAndGet();
        }

        public void incrementMiss() {
            misses.incrementAndGet();
        }

        public int getHits() {
            return hits.get();
        }

        public int getMisses() {
            return misses.get();
        }

        public double getHitRate() {
            int total = hits.get() + misses.get();
            return total > 0 ? (double) hits.get() / total * 100 : 0;
        }
    }
}