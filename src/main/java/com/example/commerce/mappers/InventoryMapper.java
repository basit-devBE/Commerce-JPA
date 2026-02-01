package com.example.commerce.mappers;

import com.example.commerce.dtos.responses.InventoryResponseDTO;
import com.example.commerce.entities.InventoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    InventoryResponseDTO toResponseDTO(InventoryEntity inventoryEntity);
}
