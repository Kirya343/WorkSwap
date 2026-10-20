package org.workswap.review.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.review.dto.MyReviews;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByListingId(Long listingId) {
        return reviewRepository.findByListingIdOrderByCreatedAtDesc(listingId); // Получаем отзывы для объявления
    }

    public List<Review> getReviewsByProfileSub(String profileSub) {
        return reviewRepository.findByProfileSubOrderByCreatedAtDesc(profileSub); // Получаем отзывы для объявления
    }

    public List<ReviewDTO> getRewiewsList(Long listingId, String profileSub) {
        List<Review> reviews = new ArrayList<>();
        if (listingId != null) {
            reviews = reviewRepository.findByListingIdOrderByCreatedAtDesc(listingId);
        } else if (profileSub != null) {
            reviews = reviewRepository.findByProfileSubOrderByCreatedAtDesc(profileSub);
        }

        return ReviewDTO.ofList(reviews);
    }

    public Page<ReviewDTO> getRewiewsPage(int page, int size, String sortParam) {

        if (sortParam == null || sortParam.length() == 0) sortParam = "createdAt";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam).descending());
        Page<Review> reviews = reviewRepository.findAll(pageable);

        return reviews.map(r -> ReviewDTO.ofReview(r));
    }

    public MyReviews getMyReviews(UserAuthData authData) {
        List<Review> given = reviewRepository.findByAuthorSub(authData.sub());
        List<Review> recived = reviewRepository.findByProfileSubOrderByCreatedAtDesc(authData.sub());

        return new MyReviews(
            ReviewDTO.ofList(given), 
            ReviewDTO.ofList(recived)
        );
    }
}
