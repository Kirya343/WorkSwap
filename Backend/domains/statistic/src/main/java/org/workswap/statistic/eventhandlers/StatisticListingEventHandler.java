package org.workswap.statistic.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.workswap.shared.events.listing.ListingDeletedEvent;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.statistic.amqp.producers.ListingViewProducer;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Component
@Profile("server")
@RequiredArgsConstructor
public class StatisticListingEventHandler {

    private final ListingViewProducer listingViewProducer;
    private final StatisticCommandService statisticCommandService;
    
    @EventListener
    public void handle(ListingViewedEvent event) {
        listingViewProducer.listingViewed(event);
    }

    @EventListener
    public void handle(ListingDeletedEvent event) {
        statisticCommandService.clearListingStatSnapshots(event.listingId());
    }
}
