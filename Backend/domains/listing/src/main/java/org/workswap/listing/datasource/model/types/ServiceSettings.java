package org.workswap.listing.datasource.model.types;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.enums.ServiceType;

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
public class ServiceSettings {

    public ServiceSettings(Listing listing, ServiceType type) {
        this.listing = listing;
        this.type = type;
    }

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private Listing listing;

    @Setter
    @Enumerated(EnumType.STRING)
    private ServiceType type;
 
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceCategory category;
}
