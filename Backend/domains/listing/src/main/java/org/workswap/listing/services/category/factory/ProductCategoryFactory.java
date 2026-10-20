package org.workswap.listing.services.category.factory;

import org.springframework.stereotype.Component;
import org.workswap.category.services.CategoryFactory;
import org.workswap.listing.datasource.model.category.ProductCategory;

@Component
public class ProductCategoryFactory implements CategoryFactory<ProductCategory> {

    @Override
    public ProductCategory create(String name, ProductCategory parent) {
        return new ProductCategory(name, parent);
    }
}
