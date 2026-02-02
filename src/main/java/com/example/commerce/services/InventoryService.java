package com.example.commerce.services;

import com.example.commerce.dtos.requests.AddInventoryDTO;
import com.example.commerce.dtos.requests.UpdateInventoryDTO;
import com.example.commerce.dtos.responses.InventoryResponseDTO;
import com.example.commerce.entities.InventoryEntity;
import com.example.commerce.entities.ProductEntity;
import com.example.commerce.errorhandlers.ResourceAlreadyExists;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import com.example.commerce.interfaces.IInventoryService;
import com.example.commerce.mappers.InventoryMapper;
import com.example.commerce.repositories.InventoryRepository;
import com.example.commerce.repositories.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InventoryService implements IInventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, 
                           ProductRepository productRepository,
                           InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @CacheEvict(value = {"inventoryById", "inventoryByProductId"}, allEntries = true)
    public InventoryResponseDTO addInventory(AddInventoryDTO addInventoryDTO) {
        // Validate product exists
        ProductEntity product = productRepository.findById(addInventoryDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + addInventoryDTO.getProductId()));

        // Check if inventory already exists for this product
        if (inventoryRepository.existsByProductId(addInventoryDTO.getProductId())) {
            throw new ResourceAlreadyExists("Inventory already exists for product ID: " + addInventoryDTO.getProductId());
        }

        InventoryEntity inventoryEntity = new InventoryEntity();
        inventoryEntity.setProduct(product);
        inventoryEntity.setQuantity(addInventoryDTO.getQuantity());
        inventoryEntity.setLocation(addInventoryDTO.getLocation());

        InventoryEntity savedInventory = inventoryRepository.save(inventoryEntity);
        return inventoryMapper.toResponseDTO(savedInventory);
    }

    public Page<InventoryResponseDTO> getAllInventories(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(inventoryMapper::toResponseDTO);
    }

    @Cacheable(value = "inventoryById", key = "#id")
    public InventoryResponseDTO getInventoryById(Long id) {
        InventoryEntity inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));
        return inventoryMapper.toResponseDTO(inventory);
    }

    @Cacheable(value = "inventoryByProductId", key = "#productId")
    public InventoryResponseDTO getInventoryByProductId(Long productId) {
        // Validate product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        InventoryEntity inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product ID: " + productId));
        return inventoryMapper.toResponseDTO(inventory);
    }

    @CacheEvict(value = {"inventoryById", "inventoryByProductId"}, allEntries = true)
    public InventoryResponseDTO updateInventory(Long id, UpdateInventoryDTO updateInventoryDTO) {
        InventoryEntity existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));

        // Only update fields that are provided
        if (updateInventoryDTO.getQuantity() != null) {
            existingInventory.setQuantity(updateInventoryDTO.getQuantity());
        }
        
        if (updateInventoryDTO.getLocation() != null) {
            existingInventory.setLocation(updateInventoryDTO.getLocation());
        }

        InventoryEntity updatedInventory = inventoryRepository.save(existingInventory);
        return inventoryMapper.toResponseDTO(updatedInventory);
    }

    public InventoryResponseDTO adjustInventoryQuantity(Long id, Integer quantityChange) {
        InventoryEntity inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));

        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient inventory. Available: " + inventory.getQuantity() + ", Required: " + Math.abs(quantityChange));
        }

        inventory.setQuantity(newQuantity);
        InventoryEntity updatedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toResponseDTO(updatedInventory);
    }

    public void deleteInventory(Long id) {
        InventoryEntity inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with ID: " + id));
        
        try {
            inventoryRepository.delete(inventory);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
                throw new com.example.commerce.errorhandlers.ConstraintViolationException(
                    "Cannot delete inventory. It has related dependencies that must be removed first.");
            }
            throw ex;
        }
    }
}
