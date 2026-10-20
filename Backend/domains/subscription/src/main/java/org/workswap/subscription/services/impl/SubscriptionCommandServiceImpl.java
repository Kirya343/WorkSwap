package org.workswap.subscription.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.enums.ListingType;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.subscription.datasource.model.Subscription;
import org.workswap.subscription.datasource.repository.SubscriptionRepository;
import org.workswap.subscription.enums.SubscriptionType;
import org.workswap.subscription.services.SubscriptionCommandService;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import jakarta.persistence.EntityManager;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    private final EntityManager entityManager;
    
    public Subscription createSubscription(UserAuthData authData, String type, Long targetId) {
        Subscription existing = subscriptionRepository.findBySubscriberSubAndTypeAndTargetId(authData.sub(), SubscriptionType.valueOf(type), targetId);
        if (targetId == null) {
            throw new IllegalStateException("У подписки нет цели!");
        }
        
        if (existing == null) { 
            SubscriptionType subType = SubscriptionType.valueOf(type);
            Subscription newSub = null;
            User subscriber = entityManager.getReference(User.class, authData.sub());
            if (subType == SubscriptionType.EVENT && listingRepository.findById(targetId).orElse(null).getType().equals(ListingType.EVENT)) {
                newSub = new Subscription(subscriber, subType, targetId);
            } else if (subType == SubscriptionType.USER && userRepository.findById(targetId).orElse(null).isOpen()) {
                newSub = new Subscription(subscriber, subType, targetId);
            }

            if (newSub == null) {
                throw new IllegalStateException("Ошибка создания подписки!");
            }

            existing = subscriptionRepository.save(newSub);
        }

        return existing;
    }

    @Transactional
    public void deleteSubscription(UserAuthData authData, SubscriptionType type, Long targetId) {
        subscriptionRepository.deleteBySubscriberSubAndTypeAndTargetId(authData.sub(), type, targetId);
    }
}
