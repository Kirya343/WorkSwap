package org.workswap.location.dto;

import java.util.Collection;
import java.util.List;

import org.workswap.location.datasource.model.Location;

public record LocationDTO (
    Long id,
    boolean city,
    Long countryId,
    String fullName,
    String name
) {
    public static LocationDTO ofLocation(Location location) {
        return new LocationDTO(
            location.getId(),
            location.isCity(),
            location.isCity() == true ? location.getCountry().getId() : null,
            location.getFullName(),
            location.getName()
        );
    }

    public static List<LocationDTO> ofList(Collection<Location> locations) {
        return locations.stream().map(location -> LocationDTO.ofLocation(location)).toList();
    }
}
