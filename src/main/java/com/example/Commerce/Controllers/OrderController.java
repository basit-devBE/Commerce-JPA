package com.example.Commerce.Controllers;

import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.*;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @RequiresRole(UserRole.USER)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody AddOrderDTO request) {
        OrderResponseDTO order = orderService.createOrder(request);
        ApiResponse<OrderResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", order);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponseDTO>>> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable);
        PagedResponse<OrderResponseDTO> pagedResponse = new PagedResponse<>(
                orders.getContent(),
                orders.getNumber(),
                (int) orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
        ApiResponse<PagedResponse<OrderResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.USER)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponseDTO>>> getOrdersByUserId(
            @PathVariable Long userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<OrderResponseDTO> orders = orderService.getOrdersByUserId(userId, pageable);
        PagedResponse<OrderResponseDTO> pagedResponse = new PagedResponse<>(
                orders.getContent(),
                orders.getNumber(),
                (int) orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
        ApiResponse<PagedResponse<OrderResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "User orders fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.USER)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        ApiResponse<OrderResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Order fetched successfully", order);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderDTO request) {
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, request);
        ApiResponse<OrderResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Order status updated successfully", updatedOrder);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Order deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
