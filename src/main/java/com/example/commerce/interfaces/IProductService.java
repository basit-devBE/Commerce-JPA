package com.example.commerce.interfaces;

import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.requests.UpdateProductDTO;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    ProductResponseDTO addProduct(AddProductDTO addProductDTO);

    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    Page<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);

    List<ProductResponseDTO> getAllProductsList();

    void deleteProduct(Long id);

    ProductResponseDTO getProductByName(String name);
}
