import { apiFetchJson, apiFetchText } from '@core/lib/common/utils/apiClient';

export const chatService = {
    getInterlocutorInfo: (id: number) => apiFetchJson(`/chat/${id}/getInterlocutorInfo`),
    deleteTemporaryChats: () => apiFetchJson("/chat/temporary", { method: "POST"}),
    getPrivateChat: (interlocutorSub: string) => apiFetchText(`/chat/private-chat`, {}, { interlocutorSub }),
    getListingDiscussion: (listingId: number) => apiFetchText(`/chat/listing-discussion`, {}, { listingId }),
    getEventChat: (eventId: number) => apiFetchText(`/chat/event-chat`, {}, { eventId })
}