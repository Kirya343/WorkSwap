package org.workswap.statistic.datasource.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.workswap.statistic.enums.StatSaveIntervalType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ListingStatSnapshot  {

    public ListingStatSnapshot(
        Long listingId,
        int views,
        int favorites,
        double rating,
        StatSaveIntervalType intervalType
    ) {
        this.listingId = listingId;
        this.views = views;
        this.favorites = favorites;
        this.rating = rating;
        this.intervalType = intervalType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long listingId;

    private int views;
    private int favorites;
    private double rating;

    @Enumerated(EnumType.STRING)
    private StatSaveIntervalType intervalType;

    @CreationTimestamp
    private LocalDateTime time;

}
