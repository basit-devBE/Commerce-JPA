package com.example.Commerce.Services;

import com.example.Commerce.DTOs.AddProductDTO;
import com.example.Commerce.DTOs.ProductResponseDTO;
import com.example.Commerce.DTOs.UpdateProductDTO;
import com.example.Commerce.Entities.CategoryEntity;
import com.example.Commerce.Entities.ProductEntity;
import com.example.Commerce.Mappers.ProductMapper;
import com.example.Commerce.Repositories.CategoryRepository;
import com.example.Commerce.Repositories.ProductRepository;
import com.example.Commerce.errorHandlers.ResourceAlreadyExists;
import com.example.Commerce.errorHandlers.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final com.example.Commerce.Repositories.InventoryRepository inventoryRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,CategoryRepository categoryRepository, com.example.Commerce.Repositories.InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public ProductResponseDTO addProduct(AddProductDTO addProductDTO){
        if(productRepository.existsByNameIgnoreCase(addProductDTO.getName())){
            throw new ResourceAlreadyExists("Product already exists");
        }
        CategoryEntity category = categoryRepository.findById(addProductDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + addProductDTO.getCategoryId()));
        ProductEntity productEntity = productMapper.toEntity(addProductDTO);
        productEntity.setCategory(category);
        ProductEntity savedProduct = productRepository.save(productEntity);
        ProductResponseDTO response =  productMapper.toResponseDTO(savedProduct);
        response.setCategoryName(savedProduct.getCategory().getName());
        
        // Set quantity from inventory if exists
        inventoryRepository.findByProductId(savedProduct.getId())
                .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
        
        return response;
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable).map(product -> {
            ProductResponseDTO response = productMapper.toResponseDTO(product);
            // Set quantity from inventory if exists
            inventoryRepository.findByProductId(product.getId())
                    .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
            return response;
        });
    }

    public Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable){
        // Validate category exists
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
        
        return productRepository.findByCategoryId(categoryId, pageable).map(product -> {
            ProductResponseDTO response = productMapper.toResponseDTO(product);
            // Set quantity from inventory if exists
            inventoryRepository.findByProductId(product.getId())
                    .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
            return response;
        });
    }

    public ProductResponseDTO getProductById(Long id){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        ProductResponseDTO response = productMapper.toResponseDTO(product);
        response.setCategoryName(product.getCategory().getName());
        
        // Set quantity from inventory if exists
        inventoryRepository.findByProductId(product.getId())
                .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
        
        return response;
    }

    public ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO){
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        // Check if name is being changed and if new name already exists
        if(updateProductDTO.getName() != null && 
           !existingProduct.getName().equalsIgnoreCase(updateProductDTO.getName()) && 
           productRepository.existsByNameIgnoreCase(updateProductDTO.getName())){
            throw new ResourceAlreadyExists("Product with name '" + updateProductDTO.getName() + "' already exists");
        }

        // Only update fields that are provided
        if(updateProductDTO.getName() != null) {
            existingProduct.setName(updateProductDTO.getName());
        }
        
        if(updateProductDTO.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(updateProductDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + updateProductDTO.getCategoryId()));
            existingProduct.setCategory(category);
        }
        
        if(updateProductDTO.getSku() != null) {
            existingProduct.setSku(updateProductDTO.getSku());
        }
        
        if(updateProductDTO.getPrice() != null) {
            existingProduct.setPrice(updateProductDTO.getPrice());
        }
        
        if(updateProductDTO.getIsAvailable() != null) {
            existingProduct.setAvailable(updateProductDTO.getIsAvailable());
        }

        ProductEntity updatedProduct = productRepository.save(existingProduct);
        ProductResponseDTO response = productMapper.toResponseDTO(updatedProduct);
        response.setCategoryName(updatedProduct.getCategory().getName());
        
        // Set quantity from inventory if exists
        inventoryRepository.findByProductId(updatedProduct.getId())
                .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
        
        return response;
    }

    public java.util.List<ProductResponseDTO> getAllProductsList() {
        return productRepository.findAll().stream().map(product -> {
            ProductResponseDTO response = productMapper.toResponseDTO(product);
            inventoryRepository.findByProductId(product.getId())
                    .ifPresent(inventory -> response.setQuantity(inventory.getQuantity()));
            return response;
        }).toList();
    }

    public void deleteProduct(Long id){
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        
        try {
            productRepository.delete(product);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
                throw new com.example.Commerce.errorHandlers.ConstraintViolationException(
                    "Cannot delete product. It is being used in orders or inventory. Please remove related records first.");
            }
            throw ex;
        }
    }
}

