"use client";

import { useEffect, useState } from "react";
import * as Popover from "@radix-ui/react-popover";
import * as Dialog from "@radix-ui/react-dialog";
import { useRouter } from "next/navigation";

import NotificationItem from "../NotificationItem/NotificationItem";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { notificationService } from "@core/lib/notification/notificationService";
import { INotification } from "@core/lib/notification/types";
import { useWebSocket } from "@core/lib/websocket/WebSocketContext";

import styles from "./NotificationsContainer.module.scss";
import { IChatMessage } from "@core/lib/chat/types";

interface NotificationsContainerProps {
    isOpen: boolean;
    onClose: () => void;
    mobile?: boolean;
}

const NotificationsContent = ({
    notifications,
    loading,
    messengerNotification,
    unreadMessages,
    onRead,
    onMessengerClick,
}: {
    notifications: INotification[] | null;
    loading: boolean;
    messengerNotification: INotification | null;
    unreadMessages: IChatMessage[] | null;
    onRead: (notification: INotification) => void;
    onMessengerClick: (notification: INotification) => void;
}) => {
    return (
        <div className={styles.content}>
            <div className={styles.list}>
                {loading && (
                    <p className={styles.empty}>
                        Загрузка...
                    </p>
                )}

                {!loading && notifications?.length === 0 && !unreadMessages?.length && (
                    <p className={styles.empty}>
                        Нет уведомлений
                    </p>
                )}

                {!loading && messengerNotification && !!unreadMessages?.length && (
                    <NotificationItem
                        notification={messengerNotification}
                        onRead={onMessengerClick}
                    />
                )}

                {!loading && notifications?.map((notification) => (
                    <NotificationItem
                        key={notification.id}
                        notification={notification}
                        onRead={onRead}
                    />
                ))}
            </div>
        </div>
    );
};

const NotificationsContainer = ({
    isOpen,
    onClose,
    mobile
}: NotificationsContainerProps) => {
    const { loading, notifications, setNotifications } = useNotification();
    const { unreadMessages } = useChats();
    const { client, connected } = useWebSocket();

    const router = useRouter();

    const [isMobile, setIsMobile] = useState(false);
    const [messengerNotification, setMessengerNotification] =
        useState<INotification | null>(null);

    useEffect(() => {
        const mediaQuery = window.matchMedia("(max-width: 600px)");

        const update = () => {
            setIsMobile(mediaQuery.matches);
        };

        update();

        mediaQuery.addEventListener("change", update);

        return () => {
            mediaQuery.removeEventListener("change", update);
        };
    }, []);

    useEffect(() => {
        if (!unreadMessages?.length) {
            setMessengerNotification(null);
            return;
        }

        setMessengerNotification({
            id: 0,
            title: `Новые сообщения: ${unreadMessages.length}`,
            content: unreadMessages[0]?.text ?? "Нажми чтобы посмотреть",
            createdAt: new Date().toISOString(),
            type: "CHAT",
            isRead: false,
            link: "/account/messenger",
            recipientId: 0,
            importance: "INFO",
        });
    }, [unreadMessages]);

    const markAsRead = async (notification: INotification) => {
        await notificationService.markAsRead(notification.id);

        setNotifications((prev) => {
            if (!prev) return prev;

            return prev.map((item) =>
                item.id === notification.id
                    ? { ...item, read: true }
                    : item
            );
        });

        if (connected && client) {
            client.publish({
                destination: `/app/notifications.readNotification/${notification.id}/`,
                body: "",
            });
        }

        router.push(notification.link);
        onClose();
    };

    const openMessenger = (notification: INotification) => {
        router.push(notification.link);
        onClose();
    };

    const content = (
        <NotificationsContent
            notifications={notifications}
            loading={loading}
            messengerNotification={messengerNotification}
            unreadMessages={unreadMessages}
            onRead={markAsRead}
            onMessengerClick={openMessenger}
        />
    );

    if (isMobile) {
        return (
            <Dialog.Root
                open={isOpen}
                onOpenChange={(open) => {
                    if (!open) {
                        onClose();
                    }
                }}
            >
                <Dialog.Portal>
                    <Dialog.Overlay className={styles.overlay} />

                    <Dialog.Content className={styles.modal}>
                        <Dialog.Title className={styles.title}>
                            Уведомления
                        </Dialog.Title>

                        <Dialog.Close asChild>
                            <button
                                type="button"
                                className={styles.close}
                                aria-label="Закрыть"
                            >
                                ✖
                            </button>
                        </Dialog.Close>

                        {content}
                    </Dialog.Content>
                </Dialog.Portal>
            </Dialog.Root>
        );
    } 
    
    if (!mobile) return (
        <Popover.Portal>
            <Popover.Content
                className={styles.popover}
                side="bottom"
                align="end"
                sideOffset={8}
            >
                {content}
            </Popover.Content>
        </Popover.Portal>
    );
};

export default NotificationsContainer;