package org.workswap.listing.controllers.category;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.services.category.ServiceCategoryService;

@RestController
@Profile("server")
@RequestMapping("/category/service")
public class ServiceCategoryController
        extends AbstractCategoryController<ServiceCategory> {

    public ServiceCategoryController(ServiceCategoryService service) {
        super(service);
    }
}