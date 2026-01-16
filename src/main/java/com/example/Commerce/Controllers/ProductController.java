package com.example.Commerce.Controllers;


import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.AddProductDTO;
import com.example.Commerce.DTOs.ApiResponse;
import com.example.Commerce.DTOs.PagedResponse;
import com.example.Commerce.DTOs.ProductResponseDTO;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

   @RequiresRole(UserRole.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> addProduct(@Valid @RequestBody AddProductDTO request){
        ProductResponseDTO product = productService.addProduct(request);
        ApiResponse<ProductResponseDTO> apiResponse = new ApiResponse<>(200, "Product added successfully", product);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponseDTO>>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ProductResponseDTO> products = productService.getAllProducts(pageable);
        PagedResponse<ProductResponseDTO> pagedResponse = new PagedResponse<>(
                products.getContent(),
                products.getNumber(),
                (int) products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
        ApiResponse<PagedResponse<ProductResponseDTO>> apiResponse = new ApiResponse<>(200, "Products fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    

}
