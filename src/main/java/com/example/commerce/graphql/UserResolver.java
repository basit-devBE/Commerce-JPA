package com.example.Commerce.graphql;

import com.example.Commerce.dtos.userSummaryDTO;
import com.example.Commerce.services.UserService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {
    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<userSummaryDTO> getAllUsers() {
        return userService.getAllUsersList();
    }

    
}
