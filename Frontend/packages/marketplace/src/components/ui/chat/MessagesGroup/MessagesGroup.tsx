import Avatar from "@core/components/common/Avatar/Avatar";
import { privateChatTypes } from "@core/lib/chat/constants/chatTypes";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { GroupedMessages, IChatMessage } from "@core/lib/chat/types";
import styles from "./MessagesGroup.module.scss"

const MessagesGroup = ({group}: {group: GroupedMessages}) => {

    const { user } = useAuth();
    const { currentChat } = useChats();
    const isOwn = (group.senderSub == user?.sub)
    const author = isOwn ? user : currentChat?.interlocutors?.find((i) => i.sub === group.senderSub) ?? null;

    return (
        <div className={`${styles.messagesGroup} ${isOwn ? styles.out : styles.in}`}>
            {!(currentChat?.type && privateChatTypes.includes(currentChat?.type)) && !isOwn && (
                <Avatar user={author} size={30} className={styles.avatar}/>
            )}
            <div className={styles.messages}>
                {group.messages?.map((message) => (
                    <Message 
                        key={message.id}
                        message={message}
                        authorName={author?.name}
                    />
                ))}
            </div>
        </div>
    )
}

const Message = ({message, authorName}: {message: IChatMessage, authorName?: string}) => {

    const date = new Date(message.sentAt ?? 0);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const formattedTime = `${hours}:${minutes}`;

    return (
        <div className={styles.message}>
            <span className={styles.authorName}>{authorName}</span>
            <div className={styles.content}>
                <span className={styles.messageText}>{message.text}</span>
                <span className={styles.messageTime}>{formattedTime}</span>
            </div>
        </div>
    );
};

export default MessagesGroup;