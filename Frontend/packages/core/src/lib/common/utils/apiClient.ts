// apiClient.js
import { API_BASE, AUTH_BASE } from "@core/config";
import FingerprintJS from '@fingerprintjs/fingerprintjs';
import Cookies from "js-cookie";

let isRefreshing: boolean = false;
let refreshPromise: Promise<Response> | null = null;

interface IApiRequest {
    method?: string, 
    headers?: {}, 
    body?: any
}

export async function apiFetchJson(url: string, options: IApiRequest = {}, extraParams = {}) {
    const res = await apiFetch(url, options, extraParams);
    return res.json();
}

export async function apiFetchText(url: string, options: IApiRequest = {}, extraParams = {}) {
    const res = await apiFetch(url, options, extraParams);
    return res.text();
}

export async function apiFetch(url: string, options: IApiRequest = {}, extraParams = {}) {

    const locale = Cookies.get("locale");
    // console.log("locale: ", locale)

    const makeRequest = async () => {

        const baseParams = { locale: locale || `en`, ...extraParams };
        const queryString = new URLSearchParams(baseParams).toString();

        const headers = {
            'Accept': 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
            ...(options.headers || {}),
        };

        const separator = url.includes("?") ? "&" : "?";

        return fetch(`${API_BASE}${url}${separator}${queryString}`, {
            ...options,
            headers,
            credentials: "include",
        });
    };

    let res = await makeRequest();

    if (res.status === 401) {
        try {
            const refreshRes = await refreshToken();
            if (refreshRes?.ok) {
                res = await makeRequest();
            }
        } catch (e) {
            console.error("Не удалось обновить токен:", e);
            throw e;
        }
    }

    if (!res.ok) {
        throw new Error(`Ошибка запроса: ${res.status} ${url}`);
    }

    return res;
}

export async function refreshToken() {
    const fingerprint = await getFingerprint();
    if (!isRefreshing) {
        isRefreshing = true;
        refreshPromise = fetch(`${AUTH_BASE}/api/auth/refresh`, {
            method: "POST",
            headers: {
                'X-Fingerprint': fingerprint
            },
            credentials: "include",
        })
            .then(res => {
                if (!res.ok) {
                    console.error(res)
                    throw new Error("Refresh failed");
                }
                return res;
            })
            .finally(() => {
                isRefreshing = false;
            });
    }
    return refreshPromise;
}

async function getFingerprint() {
    const fp = await FingerprintJS.load();
    const result = await fp.get();
    return result.visitorId; // это твой fingerprint
}