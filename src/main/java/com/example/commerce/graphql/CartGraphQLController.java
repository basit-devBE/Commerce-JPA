package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddToCartDTO;
import com.example.commerce.dtos.requests.UpdateCartItemDTO;
import com.example.commerce.dtos.responses.CartItemResponseDTO;
import com.example.commerce.dtos.responses.CartResponseDTO;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import com.example.commerce.entities.UserEntity;
import com.example.commerce.graphql.input.CartInput.AddToCartInput;
import com.example.commerce.graphql.input.CartInput.UpdateCartItemInput;
import com.example.commerce.repositories.UserRepository;
import com.example.commerce.services.CartService;
import com.example.commerce.services.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CartGraphQLController {
    private final CartService cartService;
    private final ProductService productService;
    private final UserRepository userRepository;

    public CartGraphQLController(CartService cartService, 
                                  ProductService productService,
                                  UserRepository userRepository) {
        this.cartService = cartService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public CartResponseDTO cart(@Argument Long userId) {
        return cartService.getCart(userId);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public CartResponseDTO addToCart(@Argument Long userId, @Argument AddToCartInput input) {
        AddToCartDTO dto = new AddToCartDTO();
        dto.setProductId(input.productId());
        dto.setQuantity(input.quantity());
        return cartService.addToCart(userId, dto);
    }

    @MutationMapping
    public CartResponseDTO updateCartItem(
            @Argument Long userId,
            @Argument Long productId,
            @Argument UpdateCartItemInput input) {
        UpdateCartItemDTO dto = new UpdateCartItemDTO();
        dto.setQuantity(input.quantity());
        return cartService.updateCartItem(userId, productId, dto);
    }

    @MutationMapping
    public CartResponseDTO removeFromCart(@Argument Long userId, @Argument Long productId) {
        return cartService.removeFromCart(userId, productId);
    }

    @MutationMapping
    public Boolean clearCart(@Argument Long userId) {
        cartService.clearCart(userId);
        return true;
    }

    // ==================== SCHEMA MAPPINGS (Nested Relations) ====================

    /**
     * Resolves the User for a Cart - only fetched when requested in the query
     */
    @SchemaMapping(typeName = "Cart", field = "user")
    public Map<String, Object> getCartUser(CartResponseDTO cart) {
        return userRepository.findById(cart.getUserId())
                .map(this::mapUserToGraphQL)
                .orElse(null);
    }

    /**
     * Resolves the Product for a CartItem - only fetched when requested in the query
     */
    @SchemaMapping(typeName = "CartItem", field = "product")
    public ProductResponseDTO getCartItemProduct(CartItemResponseDTO cartItem) {
        return productService.getProductById(cartItem.getProductId());
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> mapUserToGraphQL(UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().name());
        return map;
    }
}
