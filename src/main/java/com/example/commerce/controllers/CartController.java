package com.example.commerce.controllers;

import com.example.commerce.config.RequiresRole;
import com.example.commerce.dtos.requests.AddToCartDTO;
import com.example.commerce.dtos.requests.UpdateCartItemDTO;
import com.example.commerce.dtos.responses.ApiResponse;
import com.example.commerce.dtos.responses.CartResponseDTO;
import com.example.commerce.enums.UserRole;
import com.example.commerce.interfaces.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart Management")
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final ICartService cartService;

    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Get user's cart")
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SELLER})
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        CartResponseDTO cart = cartService.getCart(userId);
        ApiResponse<CartResponseDTO> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Cart fetched successfully", 
                cart
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Add item to cart")
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SELLER})
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @Valid @RequestBody AddToCartDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        CartResponseDTO cart = cartService.addToCart(userId, request);
        ApiResponse<CartResponseDTO> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Item added to cart successfully", 
                cart
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Update cart item quantity")
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SELLER})
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItem(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemDTO request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        CartResponseDTO cart = cartService.updateCartItem(userId, productId, request);
        ApiResponse<CartResponseDTO> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Cart item updated successfully", 
                cart
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Remove item from cart")
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SELLER})
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeFromCart(
            @PathVariable Long productId,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        CartResponseDTO cart = cartService.removeFromCart(userId, productId);
        ApiResponse<CartResponseDTO> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Item removed from cart successfully", 
                cart
        );
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Clear cart")
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SELLER})
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("authenticatedUserId");
        cartService.clearCart(userId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(), 
                "Cart cleared successfully", 
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
