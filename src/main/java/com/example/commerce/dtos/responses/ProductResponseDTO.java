package com.example.commerce.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResponseDTO{
    private Long id;
    private String name;
    private String sku;
    private Double price;
    private Integer quantity;
    private String categoryName;
    private boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
