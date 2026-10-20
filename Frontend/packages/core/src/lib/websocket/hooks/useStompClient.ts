"use client"

import { useCallback, useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import { API_BASE } from "@core/config";
import { useAuth } from "../../auth/AuthContext";
import { refreshToken } from "@core/lib/common/utils/apiClient";

interface UseStompClientResult {
    client: Client | null;
    connected: boolean;
    error: boolean;
}

export function useStompClient(): UseStompClientResult {
    const { user } = useAuth();

    const clientRef = useRef<Client | null>(null);
    const reconnectTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

    const [connected, setConnected] = useState(false);
    const [error, setError] = useState(false);

    const reconnectAttemptsRef = useRef(0);
    const connectingRef = useRef(false);

    const maxReconnects = 500;

    const reconnectTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

    const cleanupClient = useCallback(() => {
        if (!clientRef.current) return;

        try {
            clientRef.current.deactivate();
        } catch (e) {
            console.warn("⚠️ Error during cleanup:", e);
        }

        clientRef.current = null;
        setConnected(false);
    }, []);

    const connect = useCallback(async () => {
        if (!user || !API_BASE) return;
        if (clientRef.current?.active) return;
        if (connectingRef.current) return;

        connectingRef.current = true;

        const stompClient = new Client({

            webSocketFactory: () =>
                new WebSocket(`${API_BASE?.replace(/^http/, "ws")}/ws`),
            reconnectDelay: 0,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
        });

        stompClient.onConnect = () => {
            clientRef.current = stompClient;
            setConnected(true);
            setError(false);

            reconnectAttemptsRef.current = 0;
            connectingRef.current = false;
        };

        stompClient.onDisconnect = () => {
            connectingRef.current = false;
            setConnected(false);
        };

        stompClient.onStompError = async (frame) => {
            const message = frame?.headers?.message;

            if (!message.includes("invalidToken")) {
                setError(true);
                return;
            }

            const ok = await refreshToken();
            if (!ok?.ok) {
                setError(true);
                return;
            }

            cleanupClient();
            scheduleReconnect();
        };

        stompClient.onWebSocketClose = async (evt) => {
            setConnected(false);

            if (evt.code === 1000 || reconnectAttemptsRef.current >= maxReconnects) {
                setError(true);
                return;
            }

            const tokenRefreshed = await refreshToken();
            if (!tokenRefreshed?.ok) {
                setError(true);
                return;
            }

            scheduleReconnect();
        };

        stompClient.activate();
    }, [user, cleanupClient]);

    const scheduleReconnect = useCallback(() => {
        if (reconnectTimerRef.current) return;

        const delay = 2000 * (reconnectAttemptsRef.current + 1);

        reconnectTimerRef.current = setTimeout(() => {
            reconnectTimerRef.current = null;
            reconnectAttemptsRef.current += 1;
            connect();
        }, delay);
    }, [connect]);

    useEffect(() => {
        if (!user) return;

        connect();

        return () => {
            cleanupClient();
            if (reconnectTimer.current) {
                clearTimeout(reconnectTimer.current);
            }
        };
    }, [user, connect, cleanupClient]);

    return {
        client: clientRef.current,
        connected,
        error,
    };
}