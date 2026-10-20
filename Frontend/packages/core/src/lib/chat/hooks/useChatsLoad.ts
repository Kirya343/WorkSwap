"use client"

import { useCallback, useEffect, useRef, useState } from "react";
import { IMessage } from '@stomp/stompjs';
import { useChats } from "@core/lib/chat/MessengerContext";
import { IChatDetails } from "@core/lib/chat/types";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useWebSocket } from "@core/lib/websocket/WebSocketContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext";

export function useChatsLoad() {

    // console.log("Рендер useChatsLoad");

    const { pushDetails, pushChats, updateChat, chats } = useChats();
    const [details, setDetails] = useState<IChatDetails[] | null>(null)
    const [detalized, setDetalized] = useState<boolean>(false);
    const { isAuthenticated } = useAuth();
    const { client, connected } = useWebSocket();
    const { locale } = useI18n();

    const initializedRef = useRef(false);

    const reloadChats = useCallback(() => {
        initializedRef.current = false;
        loadChats();
    }, [client, connected, isAuthenticated]);

    const loadChats = useCallback(() => {
        if (!client || !connected || !isAuthenticated) return;
        if (initializedRef.current) return;
        // console.log("Кидаем запрос на чаты")

        initializedRef.current = true;

        client.publish({
            destination: "/app/chat.get-chats",
            body: JSON.stringify(locale)
        });
    }, [connected, isAuthenticated]);

    useEffect(() => {
        if (!details || !chats || detalized) return;
        pushDetails(details)
        setDetalized(true);
    }, [chats, detalized, details])

    useEffect(() => {

        if (!client || !connected || !isAuthenticated) return;

        const chatsSub = client.subscribe("/user/queue/chats", (message: IMessage) => {
            pushChats(JSON.parse(message.body))
            // console.log("Загрузились чаты: ", JSON.parse(message.body))
        });

        const detailsSub = client.subscribe(`/user/queue/chats/details`, (message: IMessage) => {
            setDetails(JSON.parse(message.body))
            // console.log("Пришли детали чата: ", JSON.parse(message.body))
        });

        const updatesSub = client.subscribe("/user/queue/chat.chats-updates", (message: IMessage) => {
            updateChat(JSON.parse(message.body))
        });

        loadChats();

        return () => {
            chatsSub.unsubscribe();
            detailsSub.unsubscribe();
            updatesSub.unsubscribe();
        }
    }, [connected, isAuthenticated]);

    return { reloadChats };
}