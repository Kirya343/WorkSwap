"use client";

import { AuthProvider } from "./lib/auth/AuthContext";
import { ActivePageProvider } from "./lib/common/contexts/ActivePageContext";
import { ReactNode } from "react";
import { WebSocketProvider } from "./lib/websocket/WebSocketContext";
import { MessengerProvider } from "./lib/chat/MessengerContext";
import { NotificationProvider } from "./lib/notification/NotificationContext";

export const AppProviders = ({ children }: {children: ReactNode}) => {
    return (
        <AuthProvider>
            <WebSocketProvider>
                <MessengerProvider>
                    <NotificationProvider>
                        <ActivePageProvider>
                            {children}
                        </ActivePageProvider>
                    </NotificationProvider>
                </MessengerProvider>
            </WebSocketProvider>
        </AuthProvider>
    );
};