package com.example.Commerce.mappers;

import com.example.Commerce.dtos.InventoryResponseDTO;
import com.example.Commerce.entities.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    InventoryResponseDTO toResponseDTO(InventoryEntity inventoryEntity);
}
