package org.workswap.listing.services.category.query;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.category.services.CategoryQueryService;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;

@Service
@Profile({"server", "statistic"})
public class ProductCategoryQueryService
        extends CategoryQueryService<ProductCategory>{

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryQueryService(
        CategoryRepository<ProductCategory> categoryRepository,
        ProductCategoryRepository productCategoryRepository
    ) {
        super(productCategoryRepository);
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public List<ProductCategory> findCategoryPathWithNativeQuery(Long categoryId) {
        return productCategoryRepository.findCategoryPathWithNativeQuery(categoryId);
    }
}
