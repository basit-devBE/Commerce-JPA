package com.example.Commerce.DTOs;

import com.example.Commerce.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderDTO {
    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
