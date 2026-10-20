package org.workswap.subscription.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.subscription.datasource.repository.SubscriptionRepository;
import org.workswap.subscription.enums.SubscriptionType;
import org.workswap.subscription.services.SubscriptionCommandService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscribe")
@RequiredArgsConstructor
public class SubscriptionController {
    
    private final SubscriptionCommandService subscriptionCommandService;
    private final SubscriptionRepository subscriptionRepository;

    @PostMapping("/{targetId}/add")
    @Authenticated
    public void subscribe(
        @AuthUser UserAuthData authData,
        @PathVariable Long targetId,
        @RequestParam String type
    ) {
        subscriptionCommandService.createSubscription(authData, type, targetId);
    }

    @PostMapping("/{targetId}/remove")
    @Authenticated
    public void unsubscribe(
        @AuthUser UserAuthData authData,
        @PathVariable Long targetId,
        @RequestParam String type
    ) {
        subscriptionCommandService.deleteSubscription(authData, SubscriptionType.valueOf(type), targetId);
    }

    @GetMapping("/{targetId}/check")
    @Authenticated
    public boolean checkSubscribtion(
        @AuthUser UserAuthData authData,
        @PathVariable Long targetId,
        @RequestParam String type
    ) {
        return subscriptionRepository.existsBySubscriberSubAndTypeAndTargetId(
                authData.sub(), SubscriptionType.valueOf(type), targetId);
    }
}
