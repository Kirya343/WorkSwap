import PublicListingCard from "@core/components/ui/listings/cards/PublicListingCard/PublicListingCard";
import ChatWindow from "@/components/ui/chat/ChatWindow/ChatWindow";
import DialogItem from "@/components/ui/chat/DialogItem/DialogItem";
import { ChatTypeValue } from "@core/lib/chat/constants/chatTypes";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { useChatsLoad } from "@core/lib/chat/hooks/useChatsLoad";
import { useChatSubscription } from "@core/lib/chat/hooks/useChatSubscription";
import styles from "./ChatsPage.module.scss"
import { useMemo } from "react";
import clsx from "clsx";

export default function ChatsPage({ type, targetId }: { type?: ChatTypeValue; targetId?: number; }) {

    const { chatListingVisible, chats, currentChat } = useChats();

    useChatSubscription();
    useChatsLoad();

    const { dict } = useI18n();

    const filtered = useMemo(() => {
        let result = chats ?? [];

        if (type !== undefined) {
            result = result.filter(c => c.type === type);
        }

        if (targetId !== undefined) {
            result = result.filter(c => c.targetId === targetId);
        }

        return result
            .slice()
            .sort((a, b) =>
                new Date(b.lastMessageTime).getTime() -
                new Date(a.lastMessageTime).getTime()
            );
    }, [chats, type, targetId]);

    return (
        <div className={styles.container}>
            {currentChat?.listing && (
                <div 
                    className={clsx(
                        styles.listingCardContainer, 
                        "appearance-left-animation", 
                        chatListingVisible ? "visible" : ''
                    )}
                >
                    <PublicListingCard listing={currentChat?.listing} />
                </div>
            )}

            <div className={styles.dialogsList}>
                {filtered.length === 0 ? (
                    <div className={styles.noDialogs}>
                        <p>{dict.common.messenger.placeholders.noDialogs}</p>
                        <p>{dict.common.messenger.placeholders.startChats}</p>
                    </div>
                ) : filtered.map(chat => (
                    <DialogItem key={chat.id} chat={chat} />
                ))}
            </div>
            
            <ChatWindow/>
        </div>
    )
}