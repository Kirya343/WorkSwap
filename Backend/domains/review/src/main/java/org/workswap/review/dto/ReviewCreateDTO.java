package org.workswap.review.dto;

public record ReviewCreateDTO(
    String text,
    Double rating,

    String profileSub,
    Long listingId
) {
}
