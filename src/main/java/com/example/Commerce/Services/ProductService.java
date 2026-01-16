package com.example.Commerce.Services;

import com.example.Commerce.DTOs.AddProductDTO;
import com.example.Commerce.DTOs.ProductResponseDTO;
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

    public ProductService(ProductRepository productRepository, ProductMapper productMapper,CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
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
        return response;
    }

  public Page<ProductResponseDTO> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable).map(productMapper::toResponseDTO);
  }
}

