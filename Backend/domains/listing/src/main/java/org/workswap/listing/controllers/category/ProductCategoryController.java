package org.workswap.listing.controllers.category;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.services.category.ProductCategoryService;

@RestController
@Profile("server")
@RequestMapping("/category/product")
public class ProductCategoryController
        extends AbstractCategoryController<ProductCategory> {

    public ProductCategoryController(ProductCategoryService service) {
        super(service);
    }
}