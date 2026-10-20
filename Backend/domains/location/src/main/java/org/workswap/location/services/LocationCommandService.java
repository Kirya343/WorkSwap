package org.workswap.location.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class LocationCommandService {

    private final LocationRepository locationRepository;
    
    public Location createLocation(LocationDTO location) {

        Location country;
        Location newLocation;

        if (location.countryId() != null) {
            country = locationRepository.findById(location.countryId()).orElseThrow(
                () -> new EntityNotFoundException("Локация не найдена"));

            newLocation = new Location(location.name(), true, country);
        } else {
            newLocation = new Location(location.name(), false, null);
        }

        return locationRepository.save(newLocation);
    }
}
