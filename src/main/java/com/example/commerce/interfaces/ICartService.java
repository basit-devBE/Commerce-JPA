package com.example.commerce.interfaces;

import com.example.commerce.dtos.requests.AddToCartDTO;
import com.example.commerce.dtos.requests.UpdateCartItemDTO;
import com.example.commerce.dtos.responses.CartResponseDTO;

public interface ICartService {
    CartResponseDTO getCart(Long userId);
    
    CartResponseDTO addToCart(Long userId, AddToCartDTO addToCartDTO);
    
    CartResponseDTO updateCartItem(Long userId, Long productId, UpdateCartItemDTO updateCartItemDTO);
    
    CartResponseDTO removeFromCart(Long userId, Long productId);
    
    void clearCart(Long userId);
}
