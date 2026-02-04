package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddProductDTO;
import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.dtos.responses.PagedResponse;
import com.example.commerce.dtos.responses.ProductResponseDTO;
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

    public ProductGraphQLController(ProductService productService) {
        this.productService = productService;
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
        
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "ASC";
        
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
        
        return toGraphQLPagedResponse(pagedResponse);
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

    // ==================== HELPER METHODS ====================

    private <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(PagedResponse<T> pagedResponse) {
        GraphQLPageInfo pageInfo = new GraphQLPageInfo(
            pagedResponse.currentPage(),
            pagedResponse.totalItems(),
            pagedResponse.totalPages(),
            pagedResponse.isLast(),
            !pagedResponse.isLast(),
            pagedResponse.currentPage() > 0
        );
        return GraphQLPagedResponse.of(pagedResponse.content(), pageInfo);
    }

    // ==================== INPUT RECORDS ====================

    public record AddProductInput(String name, Long categoryId, String sku, Double price) {}
    public record PaginationInput(Integer page, Integer size, String sortBy, String sortDirection) {}
}
