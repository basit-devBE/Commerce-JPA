package com.example.commerce.dtos.responses;

import lombok.Data;

@Data
public class InventoryResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String location;
}
