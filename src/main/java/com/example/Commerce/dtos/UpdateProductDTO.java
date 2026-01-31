package com.example.Commerce.dtos;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateProductDTO {
    private String name;
    private Long categoryId;
    private String sku;
    @Positive(message = "Price must be positive")
    private Double price;
    private Boolean isAvailable;
}
