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
public class ListingView {

    public ListingView(
        Long userId,
        Long listingId,
        boolean temporary,
        LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.listingId = listingId;
        this.temporary = temporary;
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;

    private Long listingId;

    private boolean temporary;

    private LocalDateTime createdAt;
}
