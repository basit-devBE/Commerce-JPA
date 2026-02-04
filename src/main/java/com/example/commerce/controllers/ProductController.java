package com.example.commerce.controllers;


import com.example.commerce.config.RequiresRole;
import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.requests.UpdateProductDTO;
import com.example.commerce.dtos.responses.ApiResponse;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import com.example.commerce.enums.UserRole;
import com.example.commerce.interfaces.IProductService;
import com.example.commerce.utils.sorting.SortingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Management")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final IProductService productService;
    private final SortingService sortingService;

    public ProductController(IProductService productService, SortingService sortingService) {
        this.productService = productService;
        this.sortingService = sortingService;
    }

    @Operation(summary = "Add a new product")
    @RequiresRole(UserRole.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addProduct(@Valid @RequestBody AddProductDTO request){
        ProductResponseDTO product = productService.addProduct(request);
        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Product added successfully", product);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get all products")
    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponseDTO>>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "ascending", defaultValue = "true") boolean ascending,
            @RequestParam(value = "algorithm", defaultValue = "QUICKSORT") String algorithm
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        PagedResponse<ProductResponseDTO> pagedResponse;
        
        if (categoryId != null) {
            pagedResponse = productService.getProductsByCategory(categoryId, pageable);
        } else {
            pagedResponse = productService.getAllProducts(pageable);
        }
        
        // Apply custom sorting if sortBy is specified
        if (sortBy != null) {
            List<ProductResponseDTO> productList = pagedResponse.content();
            try {
                SortingService.ProductSortField field = SortingService.ProductSortField.valueOf(sortBy.toUpperCase());
                SortingService.SortAlgorithm algo = SortingService.SortAlgorithm.valueOf(algorithm.toUpperCase());
                sortingService.sortProducts(productList, field, ascending, algo);
                // Create new PagedResponse with sorted content
                pagedResponse = new PagedResponse<>(
                    productList,
                    pagedResponse.currentPage(),
                    pagedResponse.totalItems(),
                    pagedResponse.totalPages(),
                    pagedResponse.isLast()
                );
            } catch (IllegalArgumentException e) {
                // Invalid sortBy or algorithm, ignore and return unsorted
            }
        }
        
        ApiResponse<PagedResponse<ProductResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Products fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id){
        ProductResponseDTO product = productService.getProductById(id);
        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Product fetched successfully", product);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update a product")
    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateProductDTO request){
        ProductResponseDTO updatedProduct = productService.updateProduct(id, request);
        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Product updated successfully", updatedProduct);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Delete a product")
    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Product deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Search product by name")
    @GetMapping("/public/search/{name} ")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductByName(@PathVariable String name){
        ProductResponseDTO product = productService.getProductByName(name);
        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Product fetched successfully", product);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/public/price-range")
    public  ResponseEntity<ApiResponse<PagedResponse<ProductResponseDTO>>> findProductByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        PagedResponse<ProductResponseDTO> pagedResponse = productService.getProductsByPriceBetween(minPrice, maxPrice, pageable);
        ApiResponse<PagedResponse<ProductResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Products fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }
}
