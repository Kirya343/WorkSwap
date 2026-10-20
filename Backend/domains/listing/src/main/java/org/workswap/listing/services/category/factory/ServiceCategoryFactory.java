package org.workswap.listing.services.category.factory;

import org.springframework.stereotype.Component;
import org.workswap.category.services.CategoryFactory;
import org.workswap.listing.datasource.model.category.ServiceCategory;

@Component
public class ServiceCategoryFactory implements CategoryFactory<ServiceCategory> {

    @Override
    public ServiceCategory create(String name, ServiceCategory parent) {
        return new ServiceCategory(name, parent);
    }
}
