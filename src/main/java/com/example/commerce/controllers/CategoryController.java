package com.example.commerce.controllers;

import com.example.commerce.config.RequiresRole;
import com.example.commerce.dtos.requests.AddCategoryDTO;
import com.example.commerce.dtos.requests.UpdateCategoryDTO;
import com.example.commerce.dtos.responses.ApiResponse;
import com.example.commerce.dtos.responses.CategoryResponseDTO;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.enums.UserRole;
import com.example.commerce.interfaces.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
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
    private final ICategoryService categoryService;

    public CategoryController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Add a new category")
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
