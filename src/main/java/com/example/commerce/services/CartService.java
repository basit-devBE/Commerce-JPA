package com.example.commerce.services;

import com.example.commerce.dtos.requests.AddToCartDTO;
import com.example.commerce.dtos.requests.UpdateCartItemDTO;
import com.example.commerce.dtos.responses.CartResponseDTO;
import com.example.commerce.entities.CartEntity;
import com.example.commerce.entities.CartItemEntity;
import com.example.commerce.entities.ProductEntity;
import com.example.commerce.entities.UserEntity;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import com.example.commerce.interfaces.ICartService;
import com.example.commerce.mappers.CartMapper;
import com.example.commerce.repositories.CartItemRepository;
import com.example.commerce.repositories.CartRepository;
import com.example.commerce.repositories.ProductRepository;
import com.example.commerce.repositories.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Cacheable(value = "cartByUserId", key = "#userId")
    public CartResponseDTO getCart(Long userId) {
        CartEntity cart = getOrCreateCart(userId);
        return mapToResponseDTO(cart);
    }

    @Override
    @CacheEvict(value = "cartByUserId", key = "#userId")
    @Transactional
    public CartResponseDTO addToCart(Long userId, AddToCartDTO addToCartDTO) {
        CartEntity cart = getOrCreateCart(userId);
        
        ProductEntity product = productRepository.findById(addToCartDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + addToCartDTO.getProductId()));
        
        if (!product.isAvailable()) {
            throw new IllegalArgumentException("Product '" + product.getName() + "' is not available");
        }

        // Check if product already in cart
        CartItemEntity existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(addToCartDTO.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + addToCartDTO.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(addToCartDTO.getQuantity());
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        // Refresh cart to get updated data
        CartEntity updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        return mapToResponseDTO(updatedCart);
    }

    @Override
    @CacheEvict(value = "cartByUserId", key = "#userId")
    @Transactional
    public CartResponseDTO updateCartItem(Long userId, Long productId, UpdateCartItemDTO updateCartItemDTO) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));

        CartItemEntity item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        if (updateCartItemDTO.getQuantity() == 0) {
            // Remove item if quantity is 0
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(updateCartItemDTO.getQuantity());
            cartItemRepository.save(item);
        }

        // Refresh cart
        CartEntity updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        return mapToResponseDTO(updatedCart);
    }

    @Override
    @CacheEvict(value = "cartByUserId", key = "#userId")
    @Transactional
    public CartResponseDTO removeFromCart(Long userId, Long productId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));

        CartItemEntity item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        
        // Refresh cart
        CartEntity updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        
        return mapToResponseDTO(updatedCart);
    }

    @Override
    @CacheEvict(value = "cartByUserId", key = "#userId")
    @Transactional
    public void clearCart(Long userId) {
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID: " + userId));
        
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartEntity getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                    
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponseDTO mapToResponseDTO(CartEntity cart) {
        CartResponseDTO dto = cartMapper.toResponseDTO(cart);
        dto.setItems(cart.getItems().stream()
                .map(cartMapper::toCartItemResponseDTO)
                .collect(Collectors.toList()));
        dto.setTotalAmount(cart.getTotalAmount());
        dto.setTotalItems(cart.getTotalItems());
        return dto;
    }
}
