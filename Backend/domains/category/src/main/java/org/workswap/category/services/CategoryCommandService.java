package org.workswap.category.services;

import org.springframework.transaction.annotation.Transactional;
import org.workswap.category.datasource.Category;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.category.dto.CategoryDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class CategoryCommandService<T extends Category> {

    protected final CategoryRepository<T> categoryRepository;
    protected final CategoryFactory<T> factory;

    @Transactional
    public Long createCategory(CategoryDTO dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new IllegalArgumentException("Category with name '" + dto.name() + "' already exists");
        }

        T parent = null;

        Long parentCategoryId = dto.parentId();
        if (parentCategoryId != null) {

            T parentCategory = categoryRepository.findById(parentCategoryId).orElse(null);
            if (parentCategory.isLeaf()) {
                throw new IllegalStateException("Cannot add subcategory to a leaf category");
            }
            parent = parentCategory;
        }

        T category = factory.create(dto.name(), parent);
        category.setLeaf(dto.leaf());

        return categoryRepository.save(category).getId();
    }

    @Transactional
    public void deleteCategory(Long categoryId) {

        if (categoryId == null) {
            throw new IllegalStateException("ID категории отсутствует");
        }

        T category = categoryRepository.findById(categoryId).orElse(null);

        if (!category.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }
        
        categoryRepository.delete(category);
    }
}
