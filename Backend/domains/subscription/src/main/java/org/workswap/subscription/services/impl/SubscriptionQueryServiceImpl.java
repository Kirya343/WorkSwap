package org.workswap.subscription.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.subscription.datasource.repository.SubscriptionRepository;
import org.workswap.subscription.enums.SubscriptionType;
import org.workswap.subscription.services.SubscriptionQueryService;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {

    private final SubscriptionRepository subscriptionRepository;

    public boolean existsEventSubscription(String subscriberSub, Long eventId) {
        return subscriptionRepository.existsBySubscriberSubAndTypeAndTargetId(subscriberSub, SubscriptionType.EVENT, eventId);
    }

    public boolean existsUserSubscription(String subscriberSub, Long userId) {
        return subscriptionRepository.existsBySubscriberSubAndTypeAndTargetId(subscriberSub, SubscriptionType.USER, userId);
    }
}
