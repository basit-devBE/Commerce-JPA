package com.example.commerce.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private Double totalAmount;
    private Integer totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
