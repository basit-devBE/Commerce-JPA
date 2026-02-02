package com.example.commerce.services;

import com.example.commerce.dtos.requests.AddOrderDTO;
import com.example.commerce.dtos.requests.OrderItemDTO;
import com.example.commerce.dtos.requests.UpdateOrderDTO;
import com.example.commerce.dtos.responses.OrderItemResponseDTO;
import com.example.commerce.dtos.responses.OrderResponseDTO;
import com.example.commerce.entities.*;
import com.example.commerce.enums.OrderStatus;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import com.example.commerce.interfaces.IOrderService;
import com.example.commerce.mappers.OrderMapper;
import com.example.commerce.repositories.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, 
                       OrderItemsRepository orderItemsRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       InventoryRepository inventoryRepository,
                       OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderMapper = orderMapper;
    }

    @CacheEvict(value = {"orderById", "inventoryById", "inventoryByProductId"}, allEntries = true)
    @Transactional
    public OrderResponseDTO createOrder(AddOrderDTO addOrderDTO) {
        // Validate user exists
        UserEntity user = userRepository.findById(addOrderDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + addOrderDTO.getUserId()));

        // Calculate total amount, validate products and check inventory
        double totalAmount = 0.0;
        List<OrderItemsEntity> orderItems = new ArrayList<>();
        List<InventoryEntity> inventoriesToUpdate = new ArrayList<>();

        for (OrderItemDTO itemDTO : addOrderDTO.getItems()) {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDTO.getProductId()));

            if (!product.isAvailable()) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' is not available");
            }

            // Check inventory
            InventoryEntity inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product '" + product.getName() + "' is out of stock"));

            if (inventory.getQuantity() < itemDTO.getQuantity()) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' is out of stock");
            }

            // Reduce inventory quantity
            inventory.setQuantity(inventory.getQuantity() - itemDTO.getQuantity());
            inventoriesToUpdate.add(inventory);

            double itemTotal = product.getPrice() * itemDTO.getQuantity();
            totalAmount += itemTotal;

            OrderItemsEntity orderItem = new OrderItemsEntity();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setTotalPrice(itemTotal);
            orderItems.add(orderItem);
        }

        // Update all inventories
        inventoryRepository.saveAll(inventoriesToUpdate);

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

    @Cacheable(value = "orderById", key = "#id")
    public OrderResponseDTO getOrderById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(order.getId());
        return buildOrderResponse(order, items);
    }

    @CacheEvict(value = "orderById", key = "#id")
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderDTO updateOrderDTO) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Only update status if provided
        if (updateOrderDTO.getStatus() != null) {
            order.setStatus(updateOrderDTO.getStatus());
        }
        
        OrderEntity updatedOrder = orderRepository.save(order);
        List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(updatedOrder.getId());
        return buildOrderResponse(updatedOrder, items);
    }

    @CacheEvict(value = "orderById", key = "#id")
    @Transactional
    public void deleteOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        try {
            // Delete order items first
            List<OrderItemsEntity> items = orderItemsRepository.findByOrderId(id);
            orderItemsRepository.deleteAll(items);

            // Delete order
            orderRepository.delete(order);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
                throw new com.example.commerce.errorhandlers.ConstraintViolationException(
                    "Cannot delete order. It has related dependencies that must be removed first.");
            }
            throw ex;
        }
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
