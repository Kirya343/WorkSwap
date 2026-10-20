import { apiFetchJson, apiFetch } from '@core/lib/common/utils/apiClient';

export const notificationService = {
    getNotificationsForUser: () => apiFetchJson(`/notification/for-user`),
    markAsRead: (notificationId: number) => apiFetch(`/notification/${notificationId}/read`, { method: "PATCH" })
}