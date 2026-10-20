package org.workswap.listing.services.category;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.category.services.CategoryFacade;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;
import org.workswap.listing.services.category.factory.ServiceCategoryFactory;
import org.workswap.listing.services.category.query.ServiceCategoryQueryService;

@Service
@Profile({"server", "statistic"})
public class ServiceCategoryService
        extends CategoryFacade<ServiceCategory> {

    public ServiceCategoryService(
        ServiceCategoryRepository repo,
        ServiceCategoryFactory factory,
        ServiceCategoryQueryService queryService
    ) {
        super(repo, factory, queryService);
    }
}