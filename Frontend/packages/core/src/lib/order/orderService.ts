import { apiFetchJson } from '@core/lib/common/utils/apiClient';

export const orderService = {
    getOrderByChat: (chatId: number) => apiFetchJson(`/order/${chatId}/chat`),
    getOrderById: (orderId: string) => apiFetchJson(`/order/${orderId}`)
}