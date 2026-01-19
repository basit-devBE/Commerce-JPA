package com.example.Commerce.DTOs;

import lombok.Data;

@Data
public class ProductResponseDTO{
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String CategoryName;
}
