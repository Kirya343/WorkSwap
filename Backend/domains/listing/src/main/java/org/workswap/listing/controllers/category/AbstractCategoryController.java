package org.workswap.listing.controllers.category;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.workswap.category.datasource.Category;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.category.services.CategoryFacade;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractCategoryController<T extends Category> {

    protected final CategoryFacade<T> categoryService;

    @GetMapping("/all")
    @PublicEndpoint
    public List<CategoryDTO> categoryList() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{parentId}/children")
    @PublicEndpoint
    public List<CategoryDTO> getChildCategories(@PathVariable Long parentId) {
        return CategoryDTO.ofList(categoryService.getChildCategories(parentId));
    }

    @GetMapping("/{categoryId}/is-leaf")
    @PublicEndpoint
    public boolean isLeafCategory(@PathVariable Long categoryId) {
        return categoryService.isLeafCategory(categoryId);
    }

    @GetMapping("/{categoryId}/path")
    @PublicEndpoint
    public List<CategoryDTO> getCategoryPath(@PathVariable Long categoryId) {
        return CategoryDTO.ofList(categoryService.getCategoryPath(categoryId));
    }

    @PostMapping
    @RequiredPermission("CREATE_CATEGORY")
    public Long createCategory(@RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @DeleteMapping("/{categoryId}")
    @RequiredPermission("DELETE_CATEGORY")
    public void deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
