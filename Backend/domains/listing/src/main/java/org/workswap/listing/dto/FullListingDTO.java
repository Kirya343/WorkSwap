package org.workswap.listing.dto;

import java.time.LocalDateTime;

import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.location.datasource.model.Location;

public record FullListingDTO(
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
    boolean liked,

    Long authorId,
    ListingPublicType publicType,
    String category,
    Long categoryId,
    Long locationId,
    Long mainImageId,
    String accessToken,
    int views,
    boolean active,
    boolean testmode,
    boolean temporary
) {
    public static FullListingDTO ofListing(Listing listing, ListingTranslation translation) {
        if (listing == null) {
            return null;
        }

        ListingType type = listing.getType();
        Location loc = listing.getLocation();

        String categoryName = "";
        Long categoryId = null;

        switch (type) {
            case SERVICE:
                ServiceSettings sSettings = listing.getServiceSettings();
                if (sSettings != null) {
                    ServiceCategory category = sSettings.getCategory();
                    if (category != null) {
                        categoryName = category.getName();
                        categoryId = category.getId();
                    }
                }
                break;

            case PRODUCT:
                ProductSettings pSettings = listing.getProductSettings();
                if (pSettings != null) {
                    ProductCategory category = pSettings.getCategory();
                    if (category != null) {
                        categoryName = category.getName();
                        categoryId = category.getId();
                    }
                }
                break;

            default: 
                break;
        }

        Image mainImage = listing.getImages()
            .stream()
            .filter(i -> 
                listing.getImagePath().equals(i.getLink())
            )
            .findFirst()
            .orElse(null);
        
        FullListingDTO dto = new FullListingDTO(
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
            false,

            listing.getAuthor().getId(),
            listing.getPublicType(),
            categoryName,
            categoryId,
            loc != null ? loc.getId() : null,
            mainImage != null ? mainImage.getId() : null,
            listing.getAccessToken(), 
            listing.getViews(),
            listing.isActive(),
            listing.isTestMode(),
            listing.isTemporary()
        );

        return dto;
    }

    public static FullListingDTO ofListingForListingPage(Listing listing, ListingTranslation translation) {
        Location loc = listing.getLocation();
        
        return new FullListingDTO(
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
            false,

            listing.getAuthor().getId(),
            listing.getPublicType(),
            null,
            null,
            loc != null ? loc.getId() : null,
            null,
            null,
            listing.getViews(),
            listing.isActive(),
            listing.isTestMode(),
            listing.isTemporary()
        );
    }
}
