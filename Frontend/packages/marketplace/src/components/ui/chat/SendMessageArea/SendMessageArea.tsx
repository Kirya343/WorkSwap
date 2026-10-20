"use client"

import { useAuth } from "@core/lib/auth/AuthContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { useWebSocket } from "@core/lib/websocket/WebSocketContext";
import { IChatMessage } from "@core/lib/chat/types";
import { useState } from "react";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./SendMessageArea.module.scss"
import Textarea from "@core/components/ui/primitives/Textarea/Textarea";

const SendMessageArea = () => {
    const { user } = useAuth();

    const { currentChatId, pushMessages } = useChats();

    const { client, connected } = useWebSocket();
    const { dict } = useI18n();
    const [message, setMessage] = useState("");

    // Проверка, можно ли писать сообщение
    const isDisabled = !currentChatId;

    const sendMessage = () => {

        if (!client || !connected || !user) return;

        if (!currentChatId) {
            alert("Пожалуйста, выберите диалог для отправки сообщения");
            return;
        }

        const trimmed = message.trim();
        if (!trimmed) return;

        const newMsg: IChatMessage = {
            id: Date.now(),
            text: trimmed,
            senderSub: user.sub,
            chatId: currentChatId,
            sentAt: new Date().toISOString(),
            read: false
        }

        pushMessages(newMsg)
        
        const msg = {
            text: trimmed,
            senderSub: user.sub,
            chatId: currentChatId
        };

        client.publish({
            destination: `/app/chat.message-send`,
            body: JSON.stringify(msg)
        });

        setMessage(""); // очищаем поле ввода
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    return (
        <div className={styles.messageInputContainer}>
            <Textarea
                value={message} 
                onChange={(e) => setMessage(e.target.value)} 
                onKeyDown={handleKeyDown}
                disabled={isDisabled}
                placeholder={isDisabled ? "" : dict.common.placeholders.typeMessage} 
                autoGrow
            />
            <button
                className={styles.sendBtn}
                onClick={sendMessage}
                disabled={isDisabled}
            >
                <img src="/images/icons/send-btn.png" alt="Отправить" />
            </button>
        </div>
    );
};
export default SendMessageArea;