import { apiFetchJson, apiFetch, apiFetchText } from '@core/lib/common/utils/apiClient';

export const getCurrent = () => apiFetchJson(`/user`);
export const getBySub = (userSub: string) => apiFetchJson(`/user/${userSub}`);
export const getUserProfile = (userOpenId: string) => apiFetchJson(`/user/${userOpenId}/profile`);
export const connectUserTelegram = () => apiFetchText('/user/telegram', {method: 'POST'});
export const checkTelegramConnected = () => apiFetchJson('/user/telegram');
export const deleteCurrentUser = () => apiFetch('/user', { method: 'DELETE'});
export const getUserSettings = () => apiFetchJson(`/user/settings`);
export const getRecentUsers = (amount: number) => apiFetchJson(`/user/recent`, {}, {amount});

export const getFullUserInfo = (userOpenId: string) => apiFetchJson(`/user/${userOpenId}/full-info`);

export const modifyUserSettings = (updates: Record<string, any>) => 
        apiFetch(`/user/modify`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updates),
        })

export const getUsersPage = (page: number, size: number, sortParam: string) => apiFetchJson(`/user/list`, {}, {page, size, sortParam});