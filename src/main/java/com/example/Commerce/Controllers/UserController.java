package com.example.Commerce.Controllers;


import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.*;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error - Invalid input data", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - Email already exists", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<userSummaryDTO>> getUserById(@PathVariable Long id) {
        userSummaryDTO user = userService.findUserById(id);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
        summary = "Update user details",
        description = "Updates the details of an existing user. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have required role", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<userSummaryDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO request) {
        userSummaryDTO updatedUser = userService.updateUser(id, request);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", updatedUser);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
        summary = "Update authenticated user's profile",
        description = "Updates the profile details of the currently logged-in user."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/updateProfile")
    public ResponseEntity<ApiResponse<userSummaryDTO>> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        userSummaryDTO updatedUser = userService.updateUser(userId, updateUserDTO);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User profile updated successfully", updatedUser);
        return ResponseEntity.ok(apiResponse);
    }
    @Operation(
        summary = "Get authenticated user's profile",
        description = "Fetches the profile details of the authenticated user."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<userSummaryDTO>> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        userSummaryDTO user = userService.findUserById(userId);
        ApiResponse<userSummaryDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User profile fetched successfully", user);
        return ResponseEntity.ok(apiResponse);
    }



    @Operation(
        summary = "Get all users",
        description = "Retrieves a paginated list of all users. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have required role", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiresRole(UserRole.ADMIN)
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

    @Operation(
        summary = "Delete a user",
        description = "Deletes an existing user. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have required role", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
