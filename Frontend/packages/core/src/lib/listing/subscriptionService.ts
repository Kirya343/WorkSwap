import { Dispatch, SetStateAction } from 'react';
import { apiFetchJson, apiFetch } from '../common/utils/apiClient';

export const subscriptionService = {
    check: (targetId: number, type: string) => apiFetchJson(`/api/subscribe/${targetId}`, { method: 'GET' }, {type}),
    create: (targetId: number, type: string) => apiFetch(`/api/subscribe/${targetId}`, { method: 'POST' }, {type}),
    delete: (targetId: number, type: string) => apiFetch(`/api/subscribe/${targetId}`, { method: 'DELETE' }, {type}),

    toggle: async (id: number, setSubscribed: Dispatch<SetStateAction<boolean>>, subscribed: boolean, type: string, e: any) => {
        if (e) e.stopPropagation();
        setSubscribed(!subscribed); // мгновенный отклик
        try {
            let data;
            if (subscribed) {
                data = await subscriptionService.delete(id, type);
            } else {
                data = await subscriptionService.create(id, type);
            }
            if (!data.ok) {
                setSubscribed(subscribed);
            }
        } catch (e) {
            setSubscribed(subscribed);
            console.error(e);
        }
    }
}