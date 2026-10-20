"use client"

import { createContext, Dispatch, SetStateAction, useContext, useState, useCallback, useMemo, ReactNode } from "react";
import { INotification, IPopupNotification } from "./types";
import { useNotificationSubscription } from "@core/lib/notification/hooks/useNotificationSubscription";
import { useChats } from "@core/lib/chat/MessengerContext";
import { useI18n } from "../common/contexts/I18nContext";
import PopupNotificationList from "@core/components/ui/notifications/PopupNotificationList/PopupNotificationList";

interface NotificationContextProps {
    notificate: (message: string, type?: string) => void;
    notificateFromRes: (res: { message: string; status: string; }) => void;
    notifications: INotification[] | null; 
    loading: boolean;
    setNotifications: Dispatch<SetStateAction<INotification[] | null>>;
    unreadNotificationsCount: number;
    popupNotifications: IPopupNotification[] | null;
    deletePopupNotification: (id: number) => void;
}

const NotificationContext = createContext<NotificationContextProps | null>(null);

export const useNotification = () => {
    const ctx = useContext(NotificationContext);
    if (!ctx) {
        throw new Error("useNotification must be used inside NotificationProvider");
    }
    return ctx;
}

export const NotificationProvider = ({ children }: {children?: ReactNode}) => {
    const { loading, notifications, setNotifications, unreadCount } = useNotificationSubscription();

    const [popupNotifications, setPopupNotifications] = useState<IPopupNotification[]>([]);
    const { unreadMessages } = useChats();
    const { dict } = useI18n();

    const unreadNotificationsCount = useMemo(() => {
        return unreadCount + (unreadMessages?.length ?? 0)
    }, [unreadCount, unreadMessages]);

    const deletePopupNotification = useCallback((id: number) => {
        setPopupNotifications((prev) => prev.filter(n => n.id !== id));
    }, []);

    const notificate = useCallback((message: string, type = "info") => {
        const id = Date.now();
        const notification = { id, message, type };
        setPopupNotifications((prev) => [...prev, notification]);

        setTimeout(() => {
            deletePopupNotification(id);
        }, 5000);
    }, [deletePopupNotification]);

    const notificateFromRes = useCallback((res: {message: string, status: string}) => {
        const id = Date.now();
        const notification = { 
            id,
            message: dict.messages.notification[res.status][res.message],
            type: res.status || ""
        };
        setPopupNotifications((prev) => [...prev, notification]);

        setTimeout(() => {
            deletePopupNotification(id);
        }, 30000);
    }, [deletePopupNotification]);

    return (
        <NotificationContext.Provider value={{
                notificate, 
                notificateFromRes, 
                notifications, 
                loading, 
                setNotifications, 
                unreadNotificationsCount,
                deletePopupNotification,
                popupNotifications
            }}
        >
            {children}
            
            <PopupNotificationList />
        </NotificationContext.Provider>
    );
};