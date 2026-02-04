package com.example.commerce.graphql;

import com.example.commerce.dtos.requests.AddCategoryDTO;
import com.example.commerce.dtos.requests.UpdateCategoryDTO;
import com.example.commerce.dtos.responses.CategoryResponseDTO;
import com.example.commerce.dtos.responses.GraphQLPagedResponse;
import com.example.commerce.graphql.input.CategoryInput.AddCategoryInput;
import com.example.commerce.graphql.input.CategoryInput.UpdateCategoryInput;
import com.example.commerce.graphql.input.PaginationInput;
import com.example.commerce.graphql.utils.GraphQLResponseMapper;
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
    private final GraphQLResponseMapper responseMapper;

    public CategoryGraphQLController(CategoryService categoryService, GraphQLResponseMapper responseMapper) {
        this.categoryService = categoryService;
        this.responseMapper = responseMapper;
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
    public GraphQLPagedResponse<CategoryResponseDTO> categoriesPaginated(@Argument PaginationInput pagination, @Argument String search) {
        int page = pagination.getPage();
        int size = pagination.getSize();
        String sortBy = pagination.getSortBy();
        String sortDir = pagination.getSortDirection();
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CategoryResponseDTO> categoriesPage;
        if (search != null && !search.isBlank()) {
            categoriesPage = categoryService.searchCategories(search, pageable);
        } else {
            categoriesPage = categoryService.getAllCategories(pageable);
        }
        return responseMapper.toGraphQLPagedResponse(categoriesPage);
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
}

