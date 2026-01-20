package com.example.Commerce.graphql;

import com.example.Commerce.DTOs.AddOrderDTO;
import com.example.Commerce.DTOs.OrderItemDTO;
import com.example.Commerce.DTOs.OrderResponseDTO;
import com.example.Commerce.DTOs.UpdateOrderDTO;
import com.example.Commerce.Enums.OrderStatus;
import com.example.Commerce.Services.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderGraphQLController {
    private final OrderService orderService;

    public OrderGraphQLController(OrderService orderService) {
        this.orderService = orderService;
    }

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

    public record AddOrderInput(Long userId, List<OrderItemInput> items) {}
    public record OrderItemInput(Long productId, Integer quantity) {}
    public record UpdateOrderInput(OrderStatus status) {}
}
