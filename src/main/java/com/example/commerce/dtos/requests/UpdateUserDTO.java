package com.example.commerce.dtos.requests;

import com.example.commerce.enums.UserRole;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    private String email;

    private UserRole role = UserRole.CUSTOMER;
}

