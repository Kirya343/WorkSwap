package org.workswap.subscription.services;

import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.subscription.datasource.model.Subscription;
import org.workswap.subscription.enums.SubscriptionType;

public interface SubscriptionCommandService {
    
    Subscription createSubscription(UserAuthData authData, String type, Long targetId);

    void deleteSubscription(UserAuthData authData, SubscriptionType type, Long targetId);
}
