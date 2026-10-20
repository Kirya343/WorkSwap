package org.workswap.listing.dto;

import java.time.LocalDateTime;

import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.location.datasource.model.Location;

public record ShortListingDTO(
    Long id,
    String localizedTitle,
    String localizedDescription,
    double price,
    PriceType priceType,
    ListingType type,
    String location,
    double rating,
    String imagePath,
    LocalDateTime publishedAt,
    long likes,
    boolean liked
) {
    public static ShortListingDTO ofListing(Listing listing, ListingTranslation translation) {
        
        if (listing == null) {
            return null;
        }

        Location loc = listing.getLocation();

        ShortListingDTO dto = new ShortListingDTO(
            listing.getId(),
            translation != null ? translation.getTitle() : null,
            translation != null ? translation.getDescription() : null,
            listing.getPrice(),
            listing.getPriceType(),
            listing.getType(),
            loc != null ? loc.getFullName() : null,
            listing.getRating(),
            listing.getImagePath(),
            listing.getPublishedAt(), 
            0,
            false
        );

        return dto;
    }

    public static ShortListingDTO updateTranslation(ShortListingDTO prev, ListingTranslation translation) {
        return new ShortListingDTO(
            prev.id(),
            translation != null ? translation.getTitle() : null,
            translation != null ? translation.getDescription() : null,
            prev.price(),
            prev.priceType(),
            prev.type(),
            prev.location(),
            prev.rating(),
            prev.imagePath(),
            prev.publishedAt(), 
            prev.likes(),
            prev.liked()
        );
    }
}
