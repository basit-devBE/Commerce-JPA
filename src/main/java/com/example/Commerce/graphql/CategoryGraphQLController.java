package com.example.Commerce.graphql;

import com.example.Commerce.DTOs.AddCategoryDTO;
import com.example.Commerce.DTOs.CategoryResponseDTO;
import com.example.Commerce.DTOs.UpdateCategoryDTO;
import com.example.Commerce.Services.CategoryService;
import org.springframework.data.domain.Pageable;
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

    @QueryMapping
    public List<CategoryResponseDTO> allCategories() {
        return categoryService.getAllCategories(Pageable.unpaged()).getContent();
    }

    @QueryMapping
    public CategoryResponseDTO categoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

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

    public record AddCategoryInput(String name, String description) {}
    public record UpdateCategoryInput(String name, String description) {}
}
