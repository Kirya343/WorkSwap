"use client"

import Avatar from "@core/components/common/Avatar/Avatar";
import { ChatType } from "@core/lib/chat/constants/chatTypes";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { IChat } from "@core/lib/chat/types";
import { useSearchParams } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import styles from "./DialogItem.module.scss"
import sharedStyles from "../ChatShared.module.scss"

interface DialogItemProps {
    chat: IChat
}

const DialogItem = ({ chat }: DialogItemProps) => {
    
    const {user} = useAuth();
    
    const searchParams = useSearchParams();
    const startChatId = Number(searchParams.get("chatId")) || null;

    const { currentChatId, setCurrentChatId, unreadMessages } = useChats();
    const [loading, setLoading] = useState(false);

    const interlocutor = chat?.interlocutors?.find(i => i.sub != user?.sub) ?? null;

    const listing = chat?.listing;

    const unreadForChat = unreadMessages?.filter(m => m.chatId === chat.id) ?? []

    const lastMessage = unreadForChat.length > 0
        ? unreadForChat.reduce((latest, msg) =>
            new Date(msg.sentAt ?? 0).getTime() > new Date(latest.sentAt ?? 0).getTime() ? msg : latest
        ) : null
        
    useEffect(() => {
        if(!loading && startChatId == chat.id) {
            setCurrentChatId(chat.id);
        }
    }, [setCurrentChatId, loading, chat, startChatId]);

    const formattedDate = chat.lastMessageTime 
        ? new Date(chat.lastMessageTime).toLocaleDateString('ru-RU')
        : "";

    const preview = useMemo(() => {
        if (!lastMessage?.sentAt) return chat.lastMessageText;
        return new Date(chat.lastMessageTime) > new Date(lastMessage.sentAt)
            ? chat.lastMessageText
            : lastMessage.text;
    }, [chat.lastMessageTime, chat.lastMessageText, lastMessage?.sentAt]);

    return (
        <div className={styles.box}>
            <div className={`${styles.dialog} ${chat.id === currentChatId ? styles.active : ""}`} onClick={() => setCurrentChatId(chat.id)}>
                {chat.type == ChatType.LISTING_DISCUSSION && listing ? (
                    <div className={sharedStyles.dialogAvatar}>
                        <img className={sharedStyles.listingImg} src={listing.imagePath} />
                        <Avatar user={interlocutor} size={40} className={sharedStyles.userAvatar} link={false} />
                    </div>
                ) : (
                    <Avatar user={interlocutor} size={50} link={false} />
                )}
                <div className={styles.content}>
                    <div className={styles.header}>
                        <h4>{interlocutor?.name}</h4>
                        <span className={styles.time}>{formattedDate}</span>
                    </div>
                    <div className={styles.meta}>
                        <p className={styles.preview}>{preview}</p>
                        {unreadForChat.length > 0 && <span className={styles.unreadCount}>{unreadForChat.length}</span>}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DialogItem;