package org.workswap.listing.datasource.model.types;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.enums.ProductType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class ProductSettings {
    
    public ProductSettings(Listing listing, ProductType type) {
        this.listing = listing;
        this.type = type;
    }

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private Listing listing;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductCategory category;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProductType type;
}
