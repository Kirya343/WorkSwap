package org.workswap.listing.dto;

import java.util.List;

import org.workswap.listing.enums.PriceType;
import org.workswap.user.dto.ShortUserProfileDTO;

public class ListingDTO {

    public record Update(
        double price,
        PriceType priceType,
        Long locationId,
        Long categoryId,
        Long mainImageId,
        String accessToken,
        boolean active,
        boolean testMode
    ) {
    }

    public record Page(
        FullListingDTO listing,
        ShortUserProfileDTO author,
        List<ImageDTO> images
    ) {}
}