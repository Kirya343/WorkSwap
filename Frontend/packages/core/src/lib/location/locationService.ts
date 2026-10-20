import { apiFetch, apiFetchJson, apiFetchText } from '@core/lib/common/utils/apiClient';
import { ILocation } from './types';

export const locationService = {
    getLocations: () => apiFetchJson("/location"),
    createLocation: (location: ILocation) => apiFetchText(
        "/location", 
        { 
            method: "POST", 
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(location)
        }),
    deleteLocation: (locationId: number) => apiFetch(`/location/${locationId}`, { method: "DELETE"})
}