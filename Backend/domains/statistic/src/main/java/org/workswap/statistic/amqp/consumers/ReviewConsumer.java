package org.workswap.statistic.amqp.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.shared.events.review.ReviewCreatedEvent;
import org.workswap.statistic.services.StatisticCommandService;
import org.workswap.user.datasource.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("statistic")
public class ReviewConsumer {
    
    private final ReviewRepository reviewRepository;
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "reviewsQueue")
    public void reviewsQueue(ReviewCreatedEvent event) {
        Review review = reviewRepository.findById(event.reviewId()).orElseThrow();

        Listing listing = review.getListing();
        User profile = review.getProfile();

        if (listing != null) {
            statisticCommandService.updateRatingForListing(listing);
        } else if (profile != null) {
            statisticCommandService.updateRatingForUser(profile);
        }
    }
}
