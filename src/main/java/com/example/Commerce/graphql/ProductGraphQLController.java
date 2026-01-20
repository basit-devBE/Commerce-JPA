package com.example.Commerce.graphql;

import com.example.Commerce.DTOs.AddProductDTO;
import com.example.Commerce.DTOs.ProductResponseDTO;
import com.example.Commerce.Services.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphQLController {
    private final ProductService productService;

    public ProductGraphQLController(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public List<ProductResponseDTO> allProducts() {
        return productService.getAllProductsList();
    }

    @QueryMapping
    public ProductResponseDTO productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @MutationMapping
    public ProductResponseDTO addProduct(@Argument AddProductInput input) {
        AddProductDTO dto = new AddProductDTO();
        dto.setName(input.name());
        dto.setCategoryId(input.categoryId());
        dto.setSku(input.sku());
        dto.setPrice(input.price());
        return productService.addProduct(dto);
    }

    @MutationMapping
    public boolean deleteProduct(@Argument Long id) {
        productService.deleteProduct(id);
        return true;
    }

    public record AddProductInput(String name, Long categoryId, String sku, Double price) {}
}
