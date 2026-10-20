"use client"

import { useWebSocket } from "@core/lib/websocket/WebSocketContext";
import { INotification } from "@core/lib/notification/types";
import { useEffect, useState } from "react";

interface UseNotificationSubscriptionReturn {
    notifications: INotification[] | null;
    setNotifications: React.Dispatch<React.SetStateAction<INotification[] | null>>;
    loading: boolean;
    unreadCount: number;
}

export function useNotificationSubscription(): UseNotificationSubscriptionReturn {
    const { client, connected } = useWebSocket();
    const [notifications, setNotifications] = useState<INotification[] | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [unreadCount, setUnreadCount] = useState<number>(0);

    // Обновление количества непрочитанных уведомлений
    useEffect(() => {
        if (!notifications) return;

        const newUnreadCount = notifications.filter(n => !n.isRead).length;

        if (unreadCount > newUnreadCount) {
            setTimeout(() => {
                setUnreadCount(prev => Math.max(prev - 1, 0));
            }, 70);
        }
    }, [notifications, unreadCount]);

    // Подписка на уведомления
    useEffect(() => {
        if (!client || !connected || !client.active) return;

        const historySubscription = client.subscribe(
            "/user/queue/notifications/history.notifications",
            (response: { body: string }) => {
                const data = JSON.parse(response.body) as INotification | INotification[];
                setLoading(false);

                if (Array.isArray(data)) {
                    setNotifications(data);
                    setUnreadCount(data.filter(n => !n.isRead).length);
                } else {
                    setNotifications(prev => prev ? [...prev, data] : [data]);
                }
            }
        );

        const liveNotificationsSubscription = client.subscribe(
            "/user/queue/notifications",
            (message: { body: string }) => {
                const data = JSON.parse(message.body) as INotification;
                console.log("Вроде пришло лайф-уведомление");
                setNotifications(prev => prev ? [...prev, data] : [data]);
                setUnreadCount(prev => prev + 1);
            }
        );

        client.publish({
            destination: "/app/notifications.loadNotifications",
            body: ""
        });
        setLoading(true);

        return () => {
            historySubscription?.unsubscribe();
            liveNotificationsSubscription?.unsubscribe();
        };
    }, [client, connected]);

    return { notifications, setNotifications, loading, unreadCount };
}