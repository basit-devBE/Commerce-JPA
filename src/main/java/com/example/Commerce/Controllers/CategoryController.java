package com.example.Commerce.Controllers;

import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.*;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category Management", description = "APIs for managing product categories")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Add a new category", description = "Creates a new product category. Requires ADMIN role.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have required role", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict - Category already exists", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiresRole(UserRole.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> addCategory(@Valid @RequestBody AddCategoryDTO request) {
        CategoryResponseDTO category = categoryService.addCategory(request);
        ApiResponse<CategoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Category added successfully", category);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponseDTO>>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<CategoryResponseDTO> categories = categoryService.getAllCategories(pageable);
        PagedResponse<CategoryResponseDTO> pagedResponse = new PagedResponse<>(
                categories.getContent(),
                categories.getNumber(),
                (int) categories.getTotalElements(),
                categories.getTotalPages(),
                categories.isLast()
        );
        ApiResponse<PagedResponse<CategoryResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Categories fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        ApiResponse<CategoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Category fetched successfully", category);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryDTO request) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, request);
        ApiResponse<CategoryResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Category updated successfully", updatedCategory);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Category deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
