package com.example.commerce.interfaces;

import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.requests.UpdateProductDTO;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    ProductResponseDTO addProduct(AddProductDTO addProductDTO);

    PagedResponse<ProductResponseDTO> getAllProducts(Pageable pageable);

    PagedResponse<ProductResponseDTO> getProductsByCategory(Long categoryId, Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO updateProduct(Long id, UpdateProductDTO updateProductDTO);

    List<ProductResponseDTO> getAllProductsList();

    void deleteProduct(Long id);

    ProductResponseDTO getProductByName(String name);
}
