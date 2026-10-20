package org.workswap.category.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.category.datasource.Category;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.category.dto.CategoryDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile({"server", "statistic"})
public abstract class CategoryQueryService<T extends Category> {

    protected final CategoryRepository<T> categoryRepository;

    @Transactional(readOnly = true)
    public List<T> getLeafCategories() {
        return categoryRepository.findByLeaf(true);
    }

    @Transactional(readOnly = true)
    public List<T> getChildCategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public List<T> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<T> getCategoryPath(Long categoryId) {
        if (categoryId == null) return Collections.emptyList();
        return findCategoryPathWithNativeQuery(categoryId);
    }

    /** 
     * Этот метод должен реализовываться в подклассах, 
     * т.к. SQL-запрос для каждой таблицы категорий будет свой 
     */
    protected abstract List<T> findCategoryPathWithNativeQuery(Long categoryId);

    @Transactional(readOnly = true)
    public List<T> getAllDescendants(T parent) {
        List<T> allCategories = categoryRepository.findAll();

        Map<Long, List<T>> parentMap = allCategories.stream()
            .filter(c -> c.getParent() != null)
            .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        List<T> result = new ArrayList<>();
        collectDescendants(parent, parentMap, result);
        return result;
    }

    @Transactional(readOnly = true)
    public List<T> getAllDescendantsById(Long parentId) {
        List<T> allCategories = categoryRepository.findAll();

        Map<Long, List<T>> parentMap = allCategories.stream()
            .filter(c -> c.getParent() != null)
            .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        List<T> result = new ArrayList<>();
        T parent = allCategories.stream()
            .filter(c -> c.getId().equals(parentId))
            .findFirst()
            .orElse(null);

        if (parent != null) {
            collectDescendants(parent, parentMap, result);
        }

        return result;
    }

    private void collectDescendants(T parent, Map<Long, List<T>> parentMap, List<T> result) {
        result.add(parent);
        List<T> children = parentMap.getOrDefault(parent.getId(), Collections.emptyList());
        for (T child : children) {
            collectDescendants(child, parentMap, result);
        }
    }

    public boolean isLeafCategory(Long categoryId) {
        if (categoryId == null) return false;

        return categoryRepository.findById(categoryId)
            .map(Category::isLeaf)
            .orElseThrow(() -> new EntityNotFoundException("Category with ID " + categoryId + " not found"));
    }

    public List<CategoryDTO> getAllCategories() {
        return CategoryDTO.ofList(categoryRepository.findAll());
    }
}
