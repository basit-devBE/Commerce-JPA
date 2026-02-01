package com.example.commerce.services;

import com.example.commerce.dtos.requests.AddCategoryDTO;
import com.example.commerce.dtos.responses.CategoryResponseDTO;
import com.example.commerce.dtos.requests.UpdateCategoryDTO;
import com.example.commerce.entities.CategoryEntity;
import com.example.commerce.interfaces.ICategoryService;
import com.example.commerce.mappers.CategoryMapper;
import com.example.commerce.repositories.CategoryRepository;
import com.example.commerce.errorhandlers.ResourceAlreadyExists;
import com.example.commerce.errorhandlers.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements ICategoryService {
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
        if (updateCategoryDTO.getName() != null &&
                !existingCategory.getName().equalsIgnoreCase(updateCategoryDTO.getName()) &&
                categoryRepository.existsByNameIgnoreCase(updateCategoryDTO.getName())) {
            throw new ResourceAlreadyExists("Category with name '" + updateCategoryDTO.getName() + "' already exists");
        }

        // Only update fields that are provided
        if (updateCategoryDTO.getName() != null) {
            existingCategory.setName(updateCategoryDTO.getName());
        }
        
        if (updateCategoryDTO.getDescription() != null) {
            existingCategory.setDescription(updateCategoryDTO.getDescription());
        }

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        
        try {
            categoryRepository.delete(category);
        } catch (Exception ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
                throw new com.example.commerce.errorhandlers.ConstraintViolationException(
                    "Cannot delete category. It is being used by one or more products. Please remove or reassign the products first.");
            }
            throw ex;
        }
    }
}
