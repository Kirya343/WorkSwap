package org.workswap.category.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.category.datasource.Category;

public record CategoryDTO(
    Long id,
    String name,
    Long parentId,
    boolean leaf
) {
    public static CategoryDTO ofCategory(Category category) {
        Long parentId = category.getParent() != null
                ? category.getParent().getId()
                : null;

        return new CategoryDTO(
            category.getId(),
            category.getName(),
            parentId,
            category.isLeaf()
        );
    }

    public static List<CategoryDTO> ofList(Collection<? extends Category> categories) {
        return categories.stream().map(cat -> ofCategory(cat)).toList();
    }
}
