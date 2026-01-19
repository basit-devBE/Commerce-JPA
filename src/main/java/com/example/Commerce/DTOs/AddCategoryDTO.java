package com.example.Commerce.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryDTO {
    @NotBlank(message = "Category name is required")
    private String name;
    @NotBlank(message = "Category description is required")
    private String description;
}
