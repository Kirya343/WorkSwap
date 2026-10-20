package org.workswap.location.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.services.LocationCommandService;
import org.workswap.location.services.LocationQueryService;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/location")
public class LocationController {
    
    private final LocationQueryService locationQueryService;
    private final LocationCommandService locationCommandService;
    private final LocationRepository locationRepository;

    @GetMapping
    @PublicEndpoint
    public List<LocationDTO> getAllLocations() {
        return locationQueryService.getAllLocations();
    }

    @GetMapping("/countries")
    @PublicEndpoint
    public List<LocationDTO> getCountires() {
        return locationQueryService.getCountries();
    }

    @GetMapping("/cities/{coutryId}")
    @PublicEndpoint
    public List<LocationDTO> getCities(@PathVariable Long coutryId) {
        return locationQueryService.getCities(coutryId);
    }

    @GetMapping("/{locationId}/get")
    @PublicEndpoint
    public LocationDTO getLocation(@PathVariable Long locationId) {
        return locationQueryService.getLocation(locationId);
    }

    @PostMapping
    @RequiredPermission("CREATE_LOCATION")
    public Long createLocation(
        @RequestBody LocationDTO location
    ) {
        return locationCommandService.createLocation(location).getId();
    }

    @DeleteMapping("/{locationId}")
    @RequiredPermission("DELETE_LOCATION")
    public void deleteLocation(@PathVariable Long locationId) {
        locationRepository.deleteById(locationId);
    }
}