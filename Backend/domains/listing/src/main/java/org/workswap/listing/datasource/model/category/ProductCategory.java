package org.workswap.listing.datasource.model.category;

import java.util.ArrayList;
import java.util.List;

import org.workswap.category.datasource.Category;
import org.workswap.listing.datasource.model.types.ProductSettings;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductCategory extends Category {

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductSettings> products = new ArrayList<>();

    @Override
    public ProductCategory getParent() { return parent; }

    @Override
    public List<ProductCategory> getChildren() { return children; }

    public ProductCategory(String name, ProductCategory parent) {
        this.name = name;
        this.parent = parent;
    }
}