package com.example.Commerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Standard error response structure for all error cases")
public class ErrorResponse {
    
    @Schema(description = "Timestamp when the error occurred", example = "2026-01-19T10:30:00.000+00:00")
    private Date timestamp;
    
    @Schema(description = "HTTP status code", example = "404")
    private Integer status;
    
    @Schema(description = "Error message describing what went wrong", example = "Product not found with id: 123")
    private String message;
    
    @Schema(description = "Request path that caused the error", example = "uri=/api/products/123")
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(Date timestamp, Integer status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
