package com.example.commerce.mappers;

import com.example.commerce.dtos.responses.CartItemResponseDTO;
import com.example.commerce.dtos.responses.CartResponseDTO;
import com.example.commerce.entities.CartEntity;
import com.example.commerce.entities.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "items", ignore = true)
    CartResponseDTO toResponseDTO(CartEntity cartEntity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(source = "product.sku", target = "productSku")
    CartItemResponseDTO toCartItemResponseDTO(CartItemEntity cartItemEntity);
}
