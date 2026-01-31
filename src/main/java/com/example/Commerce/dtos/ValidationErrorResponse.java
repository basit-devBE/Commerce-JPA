package com.example.Commerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.Map;

@Schema(description = "Error response for validation failures with field-specific error messages")
public class ValidationErrorResponse {
    
    @Schema(description = "Timestamp when the validation error occurred", example = "2026-01-19T10:30:00.000+00:00")
    private Date timestamp;
    
    @Schema(description = "HTTP status code", example = "400")
    private Integer status;
    
    @Schema(description = "Map of field names to their corresponding error messages", 
            example = "{\"email\": \"must be a well-formed email address\", \"name\": \"must not be blank\"}")
    private Map<String, String> errors;
    
    @Schema(description = "Request path that caused the error", example = "uri=/api/users/register")
    private String path;

    public ValidationErrorResponse() {
    }

    public ValidationErrorResponse(Date timestamp, Integer status, Map<String, String> errors, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
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

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
