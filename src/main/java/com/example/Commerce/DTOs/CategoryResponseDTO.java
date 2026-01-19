package com.example.Commerce.DTOs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
