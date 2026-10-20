"use client"

import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { GroupedMessages, IChat, IChatDetails, IChatMessage } from "./types";
import { Order } from "../order/types";
import { useAuth } from "../auth/AuthContext";
import { useWebSocket } from "../websocket/WebSocketContext";
import useMessengerState from "./hooks/useMessengerState";


interface MessengerContextType {
    setAllMessages: React.Dispatch<React.SetStateAction<IChatMessage[] | null>>, 
    messages: GroupedMessages[] | null,
    currentChat: IChat | null,
    currentChatId: number | null,
    setCurrentChatId: React.Dispatch<React.SetStateAction<number | null>>,
    chatListingVisible: boolean,
    setChatListingVisible: React.Dispatch<React.SetStateAction<boolean>>,
    order: Order | null,
    chats: IChat[] | null,
    setChats: React.Dispatch<React.SetStateAction<IChat[] | null>>,
    pushMessages: (messages: IChatMessage[] | IChatMessage) => void,
    unreadMessages: IChatMessage[] | null,
    pushDetails: (details: IChatDetails[]) => void,
    pushChats: (loadedChats: IChat[]) => void,
    updateChat: (update: IChat) => void,
}

const MessengerContext = createContext<MessengerContextType | null>(null);

export const useChats = () => {
    const ctx = useContext(MessengerContext);
    if (!ctx) {
        throw new Error("useChats must be used inside MessengerProvider");
    }
    return ctx;
}

export const MessengerProvider = ({ children }: { children?: React.ReactNode }) => {

    const {user} = useAuth();
    const { allMessages, setAllMessages, messages, currentChat, 
        currentChatId, setCurrentChatId, chats, setChats, 
        pushMessages, pushDetails, pushChats, updateChat
    } = useMessengerState();

    const [chatListingVisible, setChatListingVisible] = useState<boolean>(false);
    const [order, setOrder] = useState<Order | null>(null);
    const { client, connected } = useWebSocket();

    const unreadMessages = useMemo<IChatMessage[] | null>(() => {
        if (!allMessages) return null;
        return allMessages.filter(m => m.read === false && m.senderSub != user?.sub);
    }, [allMessages]);

    useEffect(() => {
        setAllMessages(null);
        setCurrentChatId(null);
        setChats(null);
    }, [user]);

    useEffect(() => {
        if (!currentChatId || !client || !connected) return;

        client.publish({
            destination: `/app/chat.markAsRead/${currentChatId}`
        });

        const url = new URL(window.location.href);
        url.searchParams.set("chatId", String(currentChatId));
        window.history.pushState({}, '', url);
        
    }, [currentChatId, client, connected]);

    return (
        <MessengerContext.Provider value={{
            setAllMessages, 
            messages,
            currentChat,
            currentChatId,
            setCurrentChatId,
            chatListingVisible,
            setChatListingVisible,
            order,
            chats,
            setChats,
            pushMessages,
            unreadMessages,
            pushDetails,
            pushChats,
            updateChat
        }}>
            {children}
        </MessengerContext.Provider>
    );
};