"use client"

import { createContext, useContext } from "react";
import { Client } from "@stomp/stompjs";
import { ReactNode } from "react";
import { useStompClient } from "./hooks/useStompClient";

interface WebSocketContextProps {
    client: Client | null;
    connected: boolean;
}

const WebSocketContext = createContext<WebSocketContextProps | null>(null);

export const useWebSocket = () => {
    const ctx = useContext(WebSocketContext);
    if (!ctx) {
        throw new Error("useWebSocket must be used inside WebSocketProvider");
    }
    return ctx;
}

interface WebSocketProviderProps {
    children: ReactNode;
}

export const WebSocketProvider = ({ children }: WebSocketProviderProps) => {
    const { client, connected } = useStompClient();

    return (
        <WebSocketContext.Provider value={{ client, connected }}>
            {children}
        </WebSocketContext.Provider>
    );
};