package com.example.commerce.dtos;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double totalPrice;
}
