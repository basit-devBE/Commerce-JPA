package com.example.Commerce.Controllers;


import com.example.Commerce.DTOs.*;
import com.example.Commerce.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided details."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO request) {
        LoginResponseDTO userResponseDTO = userService.addUser(request);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User registered successfully", userResponseDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns a token upon successful login."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(@Valid @RequestBody LoginDTO request){
        LoginResponseDTO user = userService.loginUser(request);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User logged in successfully", user);
        return ResponseEntity.ok(apiResponse);
    }
    @Operation(
        summary = "Get user by ID",
        description = "Fetches user details based on the provided user ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<userSummaryDTO>> getUserById(@PathVariable Long id) {
        userSummaryDTO user = userService.findUserById(id);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", user);
        return ResponseEntity.ok(apiResponse);
    }
//    @Operation(
//        summary = "Get user by Email",
//        description = "Fetches user details based on the provided email."
//    )
//    @GetMapping("/email/{email}")
//    public ResponseEntity<ApiResponse<userSummaryDTO>> getUserByEmail(@PathVariable String email) {
//        userSummaryDTO user = userService.findUserByEmail(email);
//        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", user);
//        return ResponseEntity.ok(apiResponse);
//    }

    @Operation(
        summary = "Update user details",
        description = "Updates the details of an existing user."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<userSummaryDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO request) {
        userSummaryDTO updatedUser = userService.updateUser(id, request);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all")
   public ResponseEntity<ApiResponse<PagedResponse<userSummaryDTO>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<userSummaryDTO> usersPage = userService.getAllUsers(pageable);
        PagedResponse<userSummaryDTO> pagedResponse = new PagedResponse<>(
                usersPage.getContent(),
                usersPage.getNumber(),
                (int) usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
        ApiResponse<PagedResponse<userSummaryDTO>> apiResponse = 
                new ApiResponse<>(HttpStatus.OK.value(), "Users fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
