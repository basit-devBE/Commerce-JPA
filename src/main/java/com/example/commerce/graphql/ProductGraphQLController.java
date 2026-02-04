package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.dtos.responses.ProductResponseDTO;
import com.example.commerce.graphql.input.PaginationInput;
import com.example.commerce.graphql.input.ProductInput.AddProductInput;
import com.example.commerce.graphql.utils.GraphQLResponseMapper;
import com.example.commerce.services.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphQLController {
    private final ProductService productService;
    private final GraphQLResponseMapper responseMapper;

    public ProductGraphQLController(ProductService productService, GraphQLResponseMapper responseMapper) {
        this.productService = productService;
        this.responseMapper = responseMapper;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<ProductResponseDTO> allProducts() {
        return productService.getAllProductsList();
    }

    @QueryMapping
    public ProductResponseDTO productById(@Argument Long id) {
        return productService.getProductById(id);
    }

    @QueryMapping
    public GraphQLPagedResponse<ProductResponseDTO> productsPaginated(
            @Argument PaginationInput pagination,
            @Argument Long categoryId,
            @Argument String search) {
        
        // Handle null pagination with defaults
        if (pagination == null) {
            pagination = new PaginationInput(0, 10, "id", "ASC");
        }
        
        int page = pagination.getPage();
        int size = pagination.getSize();
        String sortBy = pagination.getSortBy();
        String sortDir = pagination.getSortDirection();
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PagedResponse<ProductResponseDTO> pagedResponse;
        if (search != null && !search.isBlank()) {
            pagedResponse = productService.searchProducts(search, pageable);
        } else if (categoryId != null) {
            pagedResponse = productService.getProductsByCategory(categoryId, pageable);
        } else {
            pagedResponse = productService.getAllProducts(pageable);
        }
        
        return responseMapper.toGraphQLPagedResponse(pagedResponse);
    }

    // ==================== MUTATIONS ====================

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
}
