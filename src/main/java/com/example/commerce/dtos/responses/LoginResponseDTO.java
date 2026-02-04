package com.example.commerce.dtos.responses;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String token;
}

