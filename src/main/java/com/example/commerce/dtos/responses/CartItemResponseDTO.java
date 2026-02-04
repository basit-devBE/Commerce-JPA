package com.example.commerce.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Double productPrice;
    private String productSku;
    private Integer quantity;
    private Double subtotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
