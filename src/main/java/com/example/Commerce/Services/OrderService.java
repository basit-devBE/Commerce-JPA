package com.example.Commerce.Services;

import com.example.Commerce.DTOs.*;
import com.example.Commerce.Entities.OrderEntity;
import com.example.Commerce.Entities.OrderItemsEntity;
import com.example.Commerce.Entities.ProductEntity;
import com.example.Commerce.Entities.UserEntity;
import com.example.Commerce.Enums.OrderStatus;
import com.example.Commerce.Mappers.OrderMapper;
import com.example.Commerce.Repositories.OrderItemsRepository;
import com.example.Commerce.Repositories.OrderRepository;
import com.example.Commerce.Repositories.ProductRepository;
import com.example.Commerce.Repositories.UserRepository;
import com.example.Commerce.errorHandlers.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, 
                       OrderItemsRepository orderItemsRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO createOrder(AddOrderDTO addOrderDTO) {
        // Validate user exists
        UserEntity user = userRepository.findById(addOrderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + addOrderDTO.getUserId()));

        // Calculate total amount and validate products
        double totalAmount = 0.0;
        List<OrderItemsEntity> orderItems = new ArrayList<>();

        for (OrderItemDTO itemDTO : addOrderDTO.getItems()) {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDTO.getProductId()));

            if (!product.isAvailable()) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' is not available");
            }

            double itemTotal = product.getPrice() * itemDTO.getQuantity();
            totalAmount += itemTotal;

            OrderItemsEntity orderItem = new OrderItemsEntity();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(itemTotal);
            orderItems.add(orderItem);
        }

        // Create and save order
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        OrderEntity savedOrder = orderRepository.save(order);

        // Save order items
        for (OrderItemsEntity item : orderItems) {
            item.setOrder(savedOrder);
        }
        List<OrderItemsEntity> savedItems = orderItemsRepository.saveAll(orderItems);

        // Build response
        return buildOrderResponse(savedOrder, savedItems);
    }

    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order -> {
            List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(order.getId());
            return buildOrderResponse(order, items);
        });
    }

    public Page<OrderResponseDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return orderRepository.findByUserId(userId, pageable).map(order -> {
            List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(order.getId());
            return buildOrderResponse(order, items);
        });
    }

    public OrderResponseDTO getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(order.getId());
        return buildOrderResponse(order, items);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderDTO updateOrderDTO) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        order.setStatus(updateOrderDTO.getStatus());
        OrderEntity updatedOrder = orderRepository.save(order);
        List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(updatedOrder.getId());
        return buildOrderResponse(updatedOrder, items);
    }

    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Delete order items first
        List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(id);
        orderItemsRepository.deleteAll(items);

        // Delete order
        orderRepository.delete(order);
    }

    private OrderResponseDTO buildOrderResponse(OrderEntity order, List<OrderItemsEntity> items) {
        OrderResponseDTO response = orderMapper.toResponseDTO(order);
        List<OrderItemResponseDTO> itemResponses = items.stream()
                .map(orderMapper::toOrderItemResponseDTO)
                .collect(Collectors.toList());
        response.setItems(itemResponses);
        return response;
    }
}
