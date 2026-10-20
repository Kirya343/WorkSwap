package org.workswap.category.services;

import org.workswap.category.datasource.Category;

public interface CategoryFactory<T extends Category> {
    T create(String name, T parent);
}
