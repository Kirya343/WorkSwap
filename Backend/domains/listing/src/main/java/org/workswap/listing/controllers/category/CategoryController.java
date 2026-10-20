package org.workswap.listing.controllers.category;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.category.dto.CategoryDTO;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/category")
public class CategoryController {
    
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    
    @GetMapping("/all")
    @PublicEndpoint
    public Map<String, List<CategoryDTO>> categoryList() {

        List<CategoryDTO> sCategories = CategoryDTO.ofList(serviceCategoryRepository.findAll());
        List<CategoryDTO> pCategories = CategoryDTO.ofList(productCategoryRepository.findAll());

        return Map.of("PRODUCT", pCategories, "SERVICE", sCategories);
    }
}