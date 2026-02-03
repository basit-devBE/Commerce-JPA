package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddCategoryDTO;
import com.example.commerce.dtos.requests.UpdateCategoryDTO;
import com.example.commerce.dtos.responses.CategoryResponseDTO;
import com.example.commerce.dtos.responses.GraphQLPageInfo;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CategoryGraphQLController {
    private final CategoryService categoryService;

    public CategoryGraphQLController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ==================== QUERIES ====================

    @QueryMapping
    public List<CategoryResponseDTO> allCategories() {
        return categoryService.getAllCategories(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public CategoryResponseDTO categoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @QueryMapping
    public GraphQLPagedResponse<CategoryResponseDTO> categoriesPaginated(@Argument PaginationInput pagination) {
        int page = pagination != null && pagination.page() != null ? pagination.page() : 0;
        int size = pagination != null && pagination.size() != null ? pagination.size() : 10;
        String sortBy = pagination != null && pagination.sortBy() != null ? pagination.sortBy() : "id";
        String sortDir = pagination != null && pagination.sortDirection() != null ? pagination.sortDirection() : "ASC";
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CategoryResponseDTO> categoriesPage = categoryService.getAllCategories(pageable);
        return toGraphQLPagedResponse(categoriesPage);
    }

    // ==================== MUTATIONS ====================

    @MutationMapping
    public CategoryResponseDTO addCategory(@Argument AddCategoryInput input) {
        AddCategoryDTO dto = new AddCategoryDTO();
        dto.setName(input.name());
        dto.setDescription(input.description());
        return categoryService.addCategory(dto);
    }

    @MutationMapping
    public CategoryResponseDTO updateCategory(@Argument Long id, @Argument UpdateCategoryInput input) {
        UpdateCategoryDTO dto = new UpdateCategoryDTO();
        dto.setName(input.name());
        dto.setDescription(input.description());
        return categoryService.updateCategory(id, dto);
    }

    @MutationMapping
    public boolean deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
        return true;
    }

    // ==================== HELPER METHODS ====================

    private <T> GraphQLPagedResponse<T> toGraphQLPagedResponse(Page<T> page) {
        GraphQLPageInfo pageInfo = new GraphQLPageInfo(
            page.getNumber(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.isLast(),
            page.hasNext(),
            page.hasPrevious()
        );
        return GraphQLPagedResponse.of(page.getContent(), pageInfo);
    }

    // ==================== INPUT RECORDS ====================

    public record AddCategoryInput(String name, String description) {}
    public record UpdateCategoryInput(String name, String description) {}
    public record PaginationInput(Integer page, Integer size, String sortBy, String sortDirection) {}
}
