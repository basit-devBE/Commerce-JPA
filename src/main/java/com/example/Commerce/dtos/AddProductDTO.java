package com.example.Commerce.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddProductDTO {
    @NotBlank(message = "Product name is required")
    private String name;
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    @NotBlank(message = "SKU is required")
    private String sku;
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private Double price;
}


