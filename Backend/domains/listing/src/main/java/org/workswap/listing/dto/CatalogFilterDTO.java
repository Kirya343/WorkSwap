package org.workswap.listing.dto;

public record CatalogFilterDTO(
    Long categoryId,
    String sortBy,
    int page,
    String searchQuery,
    boolean hasReviews,
    boolean translationsFilter,
    String location,
    String type
) {
    public CatalogFilterDTO {
        if (sortBy == null) sortBy = "date";
    }
}
