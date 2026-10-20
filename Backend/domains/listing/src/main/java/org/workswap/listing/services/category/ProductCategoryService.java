package org.workswap.listing.services.category;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.category.services.CategoryFacade;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;
import org.workswap.listing.services.category.factory.ProductCategoryFactory;
import org.workswap.listing.services.category.query.ProductCategoryQueryService;

@Service
@Profile({"server", "statistic"})
public class ProductCategoryService
        extends CategoryFacade<ProductCategory> {

    public ProductCategoryService(
        ProductCategoryRepository repo,
        ProductCategoryFactory factory,
        ProductCategoryQueryService queryService
    ) {
        super(repo, factory, queryService);
    }
}