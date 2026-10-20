package org.workswap.subscription.services;

public interface SubscriptionQueryService {
        
    boolean existsEventSubscription(String subscriberSub, Long eventId);
    boolean existsUserSubscription(String subscriberSub, Long userId);
}
