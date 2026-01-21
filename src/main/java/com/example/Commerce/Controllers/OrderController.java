package com.example.Commerce.Controllers;

import com.example.Commerce.Config.RequiresRole;
import com.example.Commerce.DTOs.*;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Services.OrderService;
import com.example.Commerce.utils.sorting.SortingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Management", description = "APIs for managing orders")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final SortingService sortingService;

    public OrderController(OrderService orderService, SortingService sortingService) {
        this.orderService = orderService;
        this.sortingService = sortingService;
    }

    @Operation(summary = "Create a new order", description = "Creates a new order for the authenticated user. Validates inventory stock before order placement.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error or insufficient inventory", 
            content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - User does not have required role", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found or out of stock", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN})
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @Valid @RequestBody AddOrderDTO request,
            HttpServletRequest httpRequest) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute("authenticatedUserId");
        request.setUserId(authenticatedUserId);
        OrderResponseDTO order = orderService.createOrder(request);
        ApiResponse<OrderResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", order);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole(UserRole.ADMIN)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponseDTO>>> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "ascending", defaultValue = "false") boolean ascending,
            @RequestParam(value = "algorithm", defaultValue = "MERGESORT") String algorithm
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable);
        List<OrderResponseDTO> orderList = orders.getContent();
        
        // Apply custom sorting if sortBy is specified
        if (sortBy != null) {
            try {
                SortingService.OrderSortField field = SortingService.OrderSortField.valueOf(sortBy.toUpperCase());
                SortingService.SortAlgorithm algo = SortingService.SortAlgorithm.valueOf(algorithm.toUpperCase());
                sortingService.sortOrders(orderList, field, ascending, algo);
            } catch (IllegalArgumentException e) {
                // Invalid sortBy or algorithm, ignore and return unsorted
            }
        }
        
        PagedResponse<OrderResponseDTO> pagedResponse = new PagedResponse<>(
                orderList,
                orders.getNumber(),
                (int) orders.getTotalElements(),
                orders.getTotalPages(),
                orders.isLast()
        );
        ApiResponse<PagedResponse<OrderResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", pagedResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @RequiresRole({UserRole.CUSTOMER, UserRole.ADMIN})
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponseDTO>>> getOrdersByUserId(
            HttpServletRequest httpRequest,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Long authenticatedUserId = (Long) httpRequest.getAttribute("authenticatedUserId");
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<OrderResponseDTO> orders = orderService.getOrdersByUserId(authenticatedUserId, pageable);
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

    @RequiresRole(UserRole.CUSTOMER)
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
