package com.example.Commerce.dtos;

import lombok.Data;

@Data
public class InventoryResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String location;
}
