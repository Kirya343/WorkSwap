package org.workswap.listing.services;

import java.util.Collection;
import java.util.List;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.FullListingDTO;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingMappingService {

    FullListingDTO toDTO(Listing listing, String locale);
    ShortListingDTO toShortDTO(Listing listing, String locale);

    void setListingCategoryMeta(Listing listing);

    List<FullListingDTO> toDTOList(Collection<Listing> listings, String locale);
    List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, String locale);
}
