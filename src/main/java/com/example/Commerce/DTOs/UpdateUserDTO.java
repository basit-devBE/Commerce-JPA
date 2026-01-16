package com.example.Commerce.DTOs;

import com.example.Commerce.Enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    private String email;

    private UserRole role = UserRole.CUSTOMER;
}

