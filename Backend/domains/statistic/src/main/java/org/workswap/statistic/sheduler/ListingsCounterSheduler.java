package org.workswap.statistic.sheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;
import org.workswap.statistic.amqp.producers.ListingsStatProducer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Profile("server")
@Component
public class ListingsCounterSheduler {
    
    private final ListingsStatProducer listingsStatProducer;
    private final ListingRepository listingRepository;

    @Value("${isTest}")
    private boolean isTest;

    @Scheduled(fixedRate = 30000)
    public void saveListingsStats() {

        if (isTest == false) {
            AllListingsStatSnapshotDTO dto = new AllListingsStatSnapshotDTO(
                listingRepository.countByTemporary(false),
                listingRepository.countByTemporary(true),
                LocalDateTime.now()
            );

            listingsStatProducer.sendListingsStat(dto);
        }
    }
}
