package com.example.commerce.controllers;


import com.example.commerce.config.RequiresRole;
import com.example.commerce.dtos.requests.LoginDTO;
import com.example.commerce.dtos.requests.UpdateUserDTO;
import com.example.commerce.dtos.requests.UserRegistrationDTO;
import com.example.commerce.dtos.responses.ApiResponse;
import com.example.commerce.dtos.responses.LoginResponseDTO;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.dtos.responses.UserSummaryDTO;
import com.example.commerce.enums.UserRole;
import com.example.commerce.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> registerUser(@Valid @RequestBody UserRegistrationDTO request) {
        LoginResponseDTO userResponseDTO = userService.addUser(request);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User registered successfully", userResponseDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(@Valid @RequestBody LoginDTO request) {
        LoginResponseDTO user = userService.loginUser(request);
        ApiResponse<LoginResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User logged in successfully", user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get authenticated user's profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        UserSummaryDTO user = userService.findUserById(userId);
        ApiResponse<UserSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User profile fetched successfully", user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update authenticated user's profile")
    @PutMapping("/updateProfile")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        UserSummaryDTO updatedUser = userService.updateUser(userId, updateUserDTO);
        ApiResponse<UserSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User profile updated successfully", updatedUser);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get all users")
    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<UserSummaryDTO>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSummaryDTO> usersPage = userService.getAllUsers(pageable);
        PagedResponse<UserSummaryDTO> pagedResponse = new PagedResponse<>(
                usersPage.getContent(),
                usersPage.getNumber(),
                (int) usersPage.getTotalElements(),
                usersPage.getTotalPages(),
                usersPage.isLast()
        );
        ApiResponse<PagedResponse<UserSummaryDTO>> apiResponse =
                new ApiResponse<>(HttpStatus.OK.value(), "Users fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> getUserById(@PathVariable Long id) {
        UserSummaryDTO user = userService.findUserById(id);
        ApiResponse<UserSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update user details")
    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserSummaryDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO request) {
        log.info("Updating user with Body: {}", request);
        UserSummaryDTO updatedUser = userService.updateUser(id, request);
        ApiResponse<UserSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Delete a user")
    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}