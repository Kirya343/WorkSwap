package org.workswap.statistic.amqp.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.events.listing.ListingViewedEvent;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class ListingViewProducer {

    private final RabbitTemplate template;

    public void listingViewed(ListingViewedEvent event) {

        template.convertAndSend("listingViewQueue", event);
    }
}
