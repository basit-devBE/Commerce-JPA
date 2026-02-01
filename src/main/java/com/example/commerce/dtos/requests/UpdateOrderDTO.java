package com.example.commerce.dtos;

import com.example.commerce.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderDTO {
    private OrderStatus status;
}
