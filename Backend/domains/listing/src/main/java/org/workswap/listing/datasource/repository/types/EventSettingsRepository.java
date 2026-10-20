package org.workswap.listing.datasource.repository.types;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.types.EventSettings;

@Repository
public interface EventSettingsRepository extends JpaRepository<EventSettings, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO event_participants(event_id, user_id)
        SELECT :eventId, u.id
        FROM users u
        WHERE u.sub = :userSub
        ON CONFLICT DO NOTHING
        """, nativeQuery = true)
    void addParticipant(
        @Param("eventId") Long eventId,
        @Param("userSub") String userSub
    );

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM event_participants ep
        USING users u
        WHERE ep.event_id = :eventId
        AND ep.user_id = u.id
        AND u.sub = :userSub
        """, nativeQuery = true)
    void removeParticipant(
        @Param("eventId") Long eventId,
        @Param("userSub") String userSub
    );
}
