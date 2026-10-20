package org.workswap.listing.datasource.repository.types;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.listing.datasource.model.types.EventSettings;

@Repository
public interface ProductSettingsRepository extends JpaRepository<EventSettings, Long> {

    
}
