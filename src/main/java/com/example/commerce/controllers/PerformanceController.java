package com.example.commerce.controllers;

import com.example.commerce.aspects.PerformanceMonitoringAspect;
import com.example.commerce.config.RequiresRole;
import com.example.commerce.dtos.responses.ApiResponse;
import com.example.commerce.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Performance Monitoring")
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {
    
    private final PerformanceMonitoringAspect performanceAspect;

    public PerformanceController(PerformanceMonitoringAspect performanceAspect) {
        this.performanceAspect = performanceAspect;
    }

    @Operation(summary = "Get database fetch times")
    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/db-metrics")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Object>>>> getDbMetrics() {
        Map<String, Map<String, Object>> metrics = performanceAspect.getDbFetchTimes();
        ApiResponse<Map<String, Map<String, Object>>> response = new ApiResponse<>(HttpStatus.OK.value(), "Performance metrics retrieved successfully", metrics);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get cache metrics")
    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/cache-metrics")
    public ResponseEntity<ApiResponse<Map<String, Map<String, Object>>>> getCacheMetrics() {
        Map<String, Map<String, Object>> metrics = performanceAspect.getCacheMetrics();
        ApiResponse<Map<String, Map<String, Object>>> response = new ApiResponse<>(HttpStatus.OK.value(), "Cache metrics retrieved successfully", metrics);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Clear performance metrics")
    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/clear-metrics")
    public ResponseEntity<ApiResponse<Void>> clearMetrics() {
        performanceAspect.clearMetrics();
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK.value(), "Performance metrics cleared successfully", null);
        return ResponseEntity.ok(response);
    }
}