package com.example.commerce.mappers;

import com.example.commerce.dtos.ReviewResponseDTO;
import com.example.commerce.entities.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "userName", expression = "java(reviewEntity.getUser().getFirstName() + \" \" + reviewEntity.getUser().getLastName())")
    @Mapping(source = "user.email", target = "userEmail")
    ReviewResponseDTO toDTO(ReviewEntity reviewEntity);
}
