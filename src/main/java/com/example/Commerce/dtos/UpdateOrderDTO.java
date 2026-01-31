package com.example.Commerce.dtos;

import com.example.Commerce.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderDTO {
    private OrderStatus status;
}
