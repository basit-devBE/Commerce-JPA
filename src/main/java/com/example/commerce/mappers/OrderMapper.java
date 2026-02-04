package com.example.commerce.mappers;

import com.example.commerce.dtos.responses.OrderItemResponseDTO;
import com.example.commerce.dtos.responses.OrderResponseDTO;
import com.example.commerce.entities.OrderEntity;
import com.example.commerce.entities.OrderItemsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "userName", expression = "java(orderEntity.getUser().getFirstName() + \" \" + orderEntity.getUser().getLastName())")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(target = "items", ignore = true)
    OrderResponseDTO toResponseDTO(OrderEntity orderEntity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDTO toOrderItemResponseDTO(OrderItemsEntity orderItemsEntity);
}
