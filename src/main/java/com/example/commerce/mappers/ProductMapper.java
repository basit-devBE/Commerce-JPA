package com.example.commerce.mappers;

import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import com.example.commerce.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "available", target = "available")
    @Mapping(target = "quantity", ignore = true)
    ProductResponseDTO toResponseDTO(ProductEntity productEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(AddProductDTO addProductDTO);
}
