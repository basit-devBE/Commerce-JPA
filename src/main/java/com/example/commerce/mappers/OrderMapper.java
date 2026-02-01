package com.example.Commerce.mappers;

import com.example.Commerce.dtos.OrderItemResponseDTO;
import com.example.Commerce.dtos.OrderResponseDTO;
import com.example.Commerce.entities.OrderEntity;
import com.example.Commerce.entities.OrderItemsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userName")
    @Mapping(target = "items", ignore = true)
    OrderResponseDTO toResponseDTO(OrderEntity orderEntity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItemsEntity orderItemsEntity);
}
