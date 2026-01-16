package com.example.Commerce.Mappers;

import com.example.Commerce.DTOs.LoginResponseDTO;
import com.example.Commerce.DTOs.UserRegistrationDTO;
import com.example.Commerce.DTOs.userSummaryDTO;
import com.example.Commerce.Entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    LoginResponseDTO toResponseDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(UserRegistrationDTO UserRegistrationDTO);

    userSummaryDTO toSummaryDTO(UserEntity userEntity);
}
