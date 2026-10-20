import { apiFetchJson, apiFetch } from '@core/lib/common/utils/apiClient';

export const eventService = {
    getEventPage: (eventId: number, token: string | null) => apiFetchJson(`/event/${eventId}`, {}, {token}),
    checkEventParticipant: (eventId: number) => apiFetchJson(`/event/${eventId}/participants/check`),
    addEventParticipant: (eventId: number) => apiFetch(`/event/${eventId}/participants`, { method: 'POST' }),
    removeEventParticipant: (eventId: number) => apiFetch(`/event/${eventId}/participants`, { method: 'DELETE' }),
    getEventSettings: (eventId: number) => apiFetchJson(`/event/${eventId}/settings`, { method: 'GET' }),
    getEventParticipants: (eventId: number) => apiFetchJson(`/event/${eventId}/participants`, { method: 'GET' }),
    modifyEvent: (eventId: number, updates: Record<string, any>) => 
        apiFetchJson(`/api/event/${eventId}/modify`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updates),
        })
}