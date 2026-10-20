package org.workswap.listing.datasource.model.category;

import java.util.ArrayList;
import java.util.List;

import org.workswap.category.datasource.Category;
import org.workswap.listing.datasource.model.types.ServiceSettings;

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
public class ServiceCategory extends Category {

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ServiceSettings> listings = new ArrayList<>();

    @Override
    public ServiceCategory getParent() { return parent; }

    @Override
    public List<ServiceCategory> getChildren() { return children; }

    public ServiceCategory(String name, ServiceCategory parent) {
        this.name = name;
        this.parent = parent;
    }
}