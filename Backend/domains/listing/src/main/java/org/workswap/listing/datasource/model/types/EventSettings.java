package org.workswap.listing.datasource.model.types;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.workswap.listing.datasource.model.EventNews;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.RecurrencePattern;
import org.workswap.user.datasource.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class EventSettings {

    public EventSettings(Listing listing) {
        this.listing = listing;
    }

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private Listing listing;

    @Setter
    private LocalDateTime eventDate;

    @Setter
    private boolean recurring = false;

    @Setter
    private boolean isPublic = true;

    @Setter
    private Integer maxParticipants;

    @Setter
    private LocalDateTime registrationCloseTime;

    @Setter
    private Integer minParticipants = 0;

    @Setter
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.RECRUITING;

    @Setter
    @Enumerated(EnumType.STRING)
    private RecurrencePattern recurrencePattern;

    @OneToMany(mappedBy = "eventSettings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventNews> news = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "event_participants",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "event_subscribers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> subscribers = new HashSet<>();
}
