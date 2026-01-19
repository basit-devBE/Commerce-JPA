package com.example.Commerce.Services;

import com.example.Commerce.DTOs.AddCategoryDTO;
import com.example.Commerce.DTOs.CategoryResponseDTO;
import com.example.Commerce.DTOs.UpdateCategoryDTO;
import com.example.Commerce.Entities.CategoryEntity;
import com.example.Commerce.Mappers.CategoryMapper;
import com.example.Commerce.Repositories.CategoryRepository;
import com.example.Commerce.errorHandlers.ResourceAlreadyExists;
import com.example.Commerce.errorHandlers.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public CategoryResponseDTO addCategory(AddCategoryDTO addCategoryDTO) {
        if (categoryRepository.existsByNameIgnoreCase(addCategoryDTO.getName())) {
            throw new ResourceAlreadyExists("Category with name '" + addCategoryDTO.getName() + "' already exists");
        }
        CategoryEntity categoryEntity = categoryMapper.toEntity(addCategoryDTO);
        CategoryEntity savedCategory = categoryRepository.save(categoryEntity);
        return categoryMapper.toResponseDTO(savedCategory);
    }

    public Page<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toResponseDTO);
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return categoryMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(Long id, UpdateCategoryDTO updateCategoryDTO) {
        CategoryEntity existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        // Check if name is being changed and if new name already exists
        if (!existingCategory.getName().equalsIgnoreCase(updateCategoryDTO.getName()) &&
                categoryRepository.existsByNameIgnoreCase(updateCategoryDTO.getName())) {
            throw new ResourceAlreadyExists("Category with name '" + updateCategoryDTO.getName() + "' already exists");
        }

        existingCategory.setName(updateCategoryDTO.getName());
        existingCategory.setDescription(updateCategoryDTO.getDescription());

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }
}
