package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddOrderDTO;
import com.example.commerce.dtos.requests.OrderItemDTO;
import com.example.commerce.dtos.requests.UpdateOrderDTO;
import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.OrderItemResponseDTO;
import com.example.commerce.dtos.responses.OrderResponseDTO;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import com.example.commerce.entities.UserEntity;
import com.example.commerce.enums.OrderStatus;
import com.example.commerce.repositories.UserRepository;
import com.example.commerce.services.OrderService;
import com.example.commerce.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderGraphQLController {
    private final OrderService orderService;
    private final ProductService productService;
    private final UserRepository userRepository;

    public OrderGraphQLController(OrderService orderService,
                                   ProductService productService,
                                   UserRepository userRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<OrderResponseDTO> allOrders() {
        return orderService.getAllOrders(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public OrderResponseDTO orderById(@Argument Long id) {
        return orderService.getOrderById(id);
    }

    @QueryMapping
    public List<OrderResponseDTO> ordersByUserId(@Argument Long userId) {
        return orderService.getOrdersByUserId(userId, Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public GraphQLPagedResponse<OrderResponseDTO> ordersPaginated(
            @Argument PaginationInput pagination,
            @Argument OrderStatus status,
            @Argument String search) {
        
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "DESC";
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponseDTO> ordersPage;
        if (search != null && !search.isBlank()) {
            ordersPage = orderService.searchOrders(search, pageable);
        } else {
            ordersPage = orderService.getAllOrders(pageable);
        }
        
        // Filter by status if provided
        if (status != null) {
            List<OrderResponseDTO> filtered = ordersPage.getContent().stream()
                    .filter(o -> o.getStatus() == status)
                    .collect(Collectors.toList());
            GraphQLPageInfo pageInfo = new GraphQLPageInfo(
                ordersPage.getNumber(),
                filtered.size(),
                ordersPage.getTotalPages(),
                ordersPage.isLast(),
                ordersPage.hasNext(),
                ordersPage.hasPrevious()
            );
            return GraphQLPagedResponse.of(filtered, pageInfo);
        }
        
        return toGraphQLPagedResponse(ordersPage);
    }

    @QueryMapping
    public GraphQLPagedResponse<OrderResponseDTO> ordersByUserIdPaginated(
            @Argument Long userId,
            @Argument PaginationInput pagination) {
        
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "DESC";
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponseDTO> ordersPage = orderService.getOrdersByUserId(userId, pageable);
        return toGraphQLPagedResponse(ordersPage);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public OrderResponseDTO createOrder(@Argument AddOrderInput input) {
        AddOrderDTO dto = new AddOrderDTO();
        dto.setUserId(input.userId());
        dto.setItems(input.items().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.productId());
                    itemDTO.setQuantity(item.quantity());
                    return itemDTO;
                })
                .collect(Collectors.toList()));
        return orderService.createOrder(dto);
    }

    @MutationMapping
    public OrderResponseDTO updateOrderStatus(@Argument Long id, @Argument UpdateOrderInput input) {
        UpdateOrderDTO dto = new UpdateOrderDTO();
        dto.setStatus(input.status());
        return orderService.updateOrderStatus(id, dto);
    }

    @MutationMapping
    public boolean deleteOrder(@Argument Long id) {
        orderService.deleteOrder(id);
        return true;
    }

    // ==================== SCHEMA MAPPINGS (Nested Relations) ====================

    /**
     * Resolves the User for an Order - only fetched when requested in the query
     */
    @SchemaMapping(typeName = "Order", field = "user")
    public Map<String, Object> getOrderUser(OrderResponseDTO order) {
        return userRepository.findById(order.getUserId())
                .map(this::mapUserToGraphQL)
                .orElse(null);
    }

    /**
     * Resolves the Product for an OrderItem - only fetched when requested in the query
     */
    @SchemaMapping(typeName = "OrderItem", field = "product")
    public ProductResponseDTO getOrderItemProduct(OrderItemResponseDTO orderItem) {
        return productService.getProductById(orderItem.getProductId());
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

    private <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(Page<T> page) {
        GraphQLPageInfo pageInfo = new GraphQLPageInfo(
            page.getNumber(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
        return GraphQLPagedResponse.of(page.getContent(), pageInfo);
    }

    // ==================== INPUT RECORDS ====================

    public record AddOrderInput(Long userId, List<OrderItemInput> items) {}
    public record OrderItemInput(Long productId, Integer quantity) {}
    public record UpdateOrderInput(OrderStatus status) {}
    public record PaginationInput(Integer page, Integer size, String sortBy, String sortDirection) {}
}
