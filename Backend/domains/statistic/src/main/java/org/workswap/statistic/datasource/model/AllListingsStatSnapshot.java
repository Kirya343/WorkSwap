package org.workswap.statistic.datasource.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class AllListingsStatSnapshot {
    
    public AllListingsStatSnapshot(
        int publichedListings,
        int tempListings,
        LocalDateTime timestamp
    ) {
        this.listingsCount = publichedListings + tempListings;
        this.publichedListings = publichedListings;
        this.tempListings = tempListings;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private int listingsCount;

    private int publichedListings;
    private int tempListings;

    private LocalDateTime timestamp;
}
