package org.workswap.review.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.workswap.review.services.ReviewCommandService;
import org.workswap.shared.events.listing.ListingDeletedEvent;

import lombok.RequiredArgsConstructor;

@Component
@Profile("server")
@RequiredArgsConstructor
public class ReviewListingEventHandler {

    private final ReviewCommandService reviewCommandService;
    
    @EventListener
    public void handle(ListingDeletedEvent event) {
        reviewCommandService.deleteReviewsByListingId(event.listingId());
    }
}
