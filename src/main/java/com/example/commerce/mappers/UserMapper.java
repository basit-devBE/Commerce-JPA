package com.example.Commerce.mappers;

import com.example.Commerce.dtos.LoginResponseDTO;
import com.example.Commerce.dtos.UserRegistrationDTO;
import com.example.Commerce.dtos.userSummaryDTO;
import com.example.Commerce.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "token", ignore = true)
    LoginResponseDTO toResponseDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserRegistrationDTO UserRegistrationDTO);

    userSummaryDTO toSummaryDTO(UserEntity userEntity);
}
