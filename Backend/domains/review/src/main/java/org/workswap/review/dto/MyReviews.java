package org.workswap.review.dto;

import java.util.List;

public record MyReviews(
    List<ReviewDTO> given,
    List<ReviewDTO> recived
) {
}
