package org.workswap.listing.datasource.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.location.datasource.model.Location;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne(
        mappedBy = "listing", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY)
    private EventSettings eventSettings;

    @Setter
    @OneToOne(
        mappedBy = "listing", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY)
    private ProductSettings productSettings;

    @Setter
    @OneToOne(
        mappedBy = "listing", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY)
    private ServiceSettings serviceSettings;

    @Setter
    @OneToMany(
        mappedBy = "listing", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    @MapKey(name = "language") 
    private Map<String, ListingTranslation> translations = new HashMap<>();

    @Setter
    @PositiveOrZero
    private double price;

    @Setter
    private String accessToken;

    @Setter
    @Enumerated(EnumType.STRING)
    private PriceType priceType = PriceType.FIXED;
    
    @Enumerated(EnumType.STRING)
    private ListingType type;

    @Enumerated(EnumType.STRING)
    private ListingPublicType publicType;

    @Setter
    private int views = 0;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime publishedAt;

    @Setter
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(
        mappedBy = "listing", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY)
    private List<Image> images;

    @ManyToMany
    @JoinTable(
        name = "favorite_listing",
        joinColumns = @JoinColumn(name = "listing_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> favoredByUsers = new HashSet<>();

    @Setter
    private double rating = 0.0;

    @Setter
    private String imagePath;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location")
    private Location location;

    @Setter
    private boolean testMode = false;

    @Setter
    private boolean temporary = true;

    @Setter
    @Transient
    private String categoryName;

    @Setter
    private Long categoryId;

    @PreRemove
    private void removeFromUsersFavorites() {
        for (User user : favoredByUsers) {
            favoredByUsers.remove(user);
        }
    }

    public Listing(User author, String type) {
        this.author = author;

        if (type != null) {
            ListingPublicType publicType = ListingPublicType.valueOf(type);
            this.publicType = publicType;
            this.type = publicType.getListingType();

            switch (this.type) {
                case SERVICE -> this.serviceSettings = new ServiceSettings(this, publicType.getServiceType());
                case PRODUCT -> this.productSettings = new ProductSettings(this, publicType.getProductType());
                case EVENT -> this.eventSettings = new EventSettings(this);
            }

            switch (this.publicType) {
                case PRODUCT_SWAP -> this.priceType = PriceType.SWAP;
                case PRODUCT_WANTED_FREE -> this.priceType = PriceType.WANTED_FREE;
                case PRODUCT_GIVEAWAY -> this.priceType = PriceType.FREE;
                default -> this.priceType = PriceType.FIXED;
            }
        }
    }
}