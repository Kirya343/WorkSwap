package org.workswap.location.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.services.LocationQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class LocationQueryServiceImpl implements LocationQueryService {
    
    private final LocationRepository locationRepository;

    public List<LocationDTO> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return LocationDTO.ofList(locations);
    }

    public List<Location> getAllDescendants(Location location) {
        List<Location> descendants = new ArrayList<>();
        descendants.add(location);
        if (!location.isCity()) {
            List<Location> cities = locationRepository.findByCountry(location);
            for (Location child : cities) {
                descendants.add(child);
            }
        }
        return descendants;
    }

    public List<LocationDTO> getCountries() {
        List<Location> locations = locationRepository.findByCity(false);
        return LocationDTO.ofList(locations);
    }

    public List<LocationDTO> getCities(Long coutryId) {
        List<Location> locations = locationRepository.findByCountryId(coutryId);
        return LocationDTO.ofList(locations);
    }

    public LocationDTO getLocation(Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow();
        return LocationDTO.ofLocation(location);
    }
}
