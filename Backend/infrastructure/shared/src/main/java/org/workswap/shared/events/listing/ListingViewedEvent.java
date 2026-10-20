package org.workswap.shared.events.listing;

import java.time.LocalDateTime;

public record ListingViewedEvent(
    String userSub,
    Long listingId,
    boolean temporary,
    LocalDateTime timestamp
) {
}
