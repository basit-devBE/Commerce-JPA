package com.example.Commerce.Mappers;

import com.example.Commerce.DTOs.AddCategoryDTO;
import com.example.Commerce.DTOs.CategoryResponseDTO;
import com.example.Commerce.Entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(CategoryEntity categoryEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CategoryEntity toEntity(AddCategoryDTO addCategoryDTO);
}
