package org.workswap.subscription.dto;

import java.time.LocalDateTime;

import org.workswap.subscription.datasource.model.Subscription;

public record SubscriptionDTO(
    Long id,
    Long userId,
    String type,
    Long targetId,
    LocalDateTime createdAt
) {
    public static SubscriptionDTO ofSubscription(Subscription subscription) {
        return new SubscriptionDTO(
            subscription.getId(),
            subscription.getSubscriber().getId(),
            subscription.getType().toString(),
            subscription.getTargetId(),
            subscription.getCreatedAt()
        );
    }
}
