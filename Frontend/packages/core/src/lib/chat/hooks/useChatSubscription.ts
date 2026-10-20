"use client"

import { useEffect, useRef } from "react";
import { IMessage } from '@stomp/stompjs';
import { useChats } from "@core/lib/chat/MessengerContext";
import { useWebSocket } from "@core/lib/websocket/WebSocketContext";
import { useAuth } from "@core/lib/auth/AuthContext";
import { IPageRequest } from "@core/lib/common/types/page";

export function useChatSubscription() {

    const { currentChatId, pushMessages } = useChats();
    const { client, connected } = useWebSocket();
    const { user, isAuthenticated } = useAuth();

    const requestedRef = useRef<Set<number>>(new Set());
    const loadedRef = useRef<Set<number>>(new Set());

    // reset при смене пользователя
    useEffect(() => {
        requestedRef.current.clear();
        loadedRef.current.clear();
    }, [user?.sub]);

    // общая подписка (новые сообщения + unread)
    useEffect(() => {
        if (!client || !connected || !isAuthenticated) return;

        const sub = client.subscribe(`/user/queue/chat/messages`, (res: IMessage) => {
            pushMessages(JSON.parse(res.body));
        });

        client.publish({ destination: `/app/messages.get-unread` });

        return () => sub.unsubscribe();
    }, [connected, isAuthenticated, pushMessages]);

    // история конкретного чата
    useEffect(() => {
        if (!client || !connected || !isAuthenticated || !currentChatId) return;

        const sub = client.subscribe(
            `/user/queue/chat/history.messages/${currentChatId}`,
            (res: IMessage) => {
                pushMessages(JSON.parse(res.body));
                loadedRef.current.add(currentChatId);
            }
        );

        return () => sub.unsubscribe();
    }, [connected, isAuthenticated, currentChatId, pushMessages]);

    // триггер загрузки истории
    useEffect(() => {
        if (!client || !connected || !isAuthenticated || !currentChatId) return;

        if (requestedRef.current.has(currentChatId)) return;

        requestedRef.current.add(currentChatId);

        const pageRequest: IPageRequest = {
            page: 0, size: 50
        }

        client.publish({ destination: `/app/chat.loadMessages/${currentChatId}`, body: JSON.stringify(pageRequest) });
    }, [connected, isAuthenticated, currentChatId]);
}