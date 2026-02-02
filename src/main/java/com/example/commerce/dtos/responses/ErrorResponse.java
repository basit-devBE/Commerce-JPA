package com.example.commerce.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Schema(description = "Standard error response structure for all error cases")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    
    @Schema(description = "Timestamp when the error occurred", example = "2026-01-19T10:30:00.000+00:00")
    private Date timestamp;
    
    @Schema(description = "HTTP status code", example = "404")
    private Integer status;
    
    @Schema(description = "Error message describing what went wrong", example = "Product not found with id: 123")
    private String message;
    
    @Schema(description = "Request path that caused the error", example = "uri=/api/products/123")
    private String path;
}
