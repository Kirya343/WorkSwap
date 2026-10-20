package org.workswap.listing.datasource.model;

import org.workswap.listing.enums.ListingTranslateType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"listing_id", "language"}
    )
)
public class ListingTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language;

    @Setter
    private String title;

    @Setter
    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "listing_id", insertable = false, updatable = false)
    private Long listingId;

    @Enumerated(EnumType.STRING)
    private ListingTranslateType type;
    
    public ListingTranslation(
            String language,
            String title,
            String description,
            Listing listing,
            ListingTranslateType type
    ) {
        this.language = language;
        this.title = title;
        this.description = description;
        this.listing = listing;
        this.type = type;
    }
}