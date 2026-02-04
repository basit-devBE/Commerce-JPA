package com.example.commerce.dtos.responses;

import com.example.commerce.enums.UserRole;
import lombok.Data;

@Data
public class userSummaryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
