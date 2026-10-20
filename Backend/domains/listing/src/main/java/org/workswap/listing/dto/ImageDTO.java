package org.workswap.listing.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.listing.datasource.model.Image;

public record ImageDTO(
    Long id,
    Long listingId,
    String path
) {
    public static ImageDTO ofImage(Image image) {
        return new ImageDTO(
            image.getId(), 
            image.getListingId(), 
            image.getLink()
        );
    }

    public static List<ImageDTO> ofList(Collection<Image> images) {
        return images.stream().map(i -> ImageDTO.ofImage(i)).toList();
    }
}
