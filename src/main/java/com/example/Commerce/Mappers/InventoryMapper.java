package com.example.Commerce.Mappers;

import com.example.Commerce.DTOs.InventoryResponseDTO;
import com.example.Commerce.Entities.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    InventoryResponseDTO toResponseDTO(InventoryEntity inventoryEntity);
}
