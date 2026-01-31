package com.example.Commerce.dtos;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class UpdateInventoryDTO {
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;
    
    private String location;
}
