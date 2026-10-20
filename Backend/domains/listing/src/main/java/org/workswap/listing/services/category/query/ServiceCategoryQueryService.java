package org.workswap.listing.services.category.query;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.category.datasource.CategoryRepository;
import org.workswap.category.services.CategoryQueryService;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;

@Service
@Profile({"server", "statistic"})
public class ServiceCategoryQueryService
        extends CategoryQueryService<ServiceCategory>{

    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategoryQueryService(
        CategoryRepository<ServiceCategory> categoryRepository,
        ServiceCategoryRepository serviceCategoryRepository) {
        super(serviceCategoryRepository);
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    @Override
    public List<ServiceCategory> findCategoryPathWithNativeQuery(Long categoryId) {
        return serviceCategoryRepository.findCategoryPathWithNativeQuery(categoryId);
    }
}
