import { useAuth } from "@core/lib/auth/AuthContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { useChatsLoad } from "@core/lib/chat/hooks/useChatsLoad";
import { chatService } from "@core/lib/chat/chatService";
import { useRouter } from "next/navigation";
import { ReactNode, useState } from "react";
import LoadingSpinnerIcon from "@core/components/common/icons/LoadingSpinnerIcon"

interface ChatLinkMessageProps {
    listingId: number | null;
    interlocutorSub: string;
    className?: string;
    children?: ReactNode;
}

const ChatLinkMessage = ({listingId, interlocutorSub, className, children}: ChatLinkMessageProps) => {

    const { dict } = useI18n();
    const router = useRouter();

    const { user } = useAuth();

    const { reloadChats } = useChatsLoad();
    const { setCurrentChatId } = useChats();
    const [loading, setLoading] = useState<boolean>(false)

    const handleClick = async () => {
        if (!interlocutorSub || !user) return;

        setLoading(true);

        let chatId: number;

        try {
            const data = listingId
                ? await chatService.getListingDiscussion(listingId)
                : await chatService.getPrivateChat(interlocutorSub);

            chatId = Number(data);

        } finally {
            setLoading(false);
        }

        // 1. сначала локальное состояние
        setCurrentChatId(chatId);

        // 2. websocket отдельно
        reloadChats();

        // 3. навигация — последняя операция
        router.push(`/account/messenger?chatId=${chatId}`);
    }

    return (
        <button onClick={handleClick} className={`${className ? className : "btn btn-primary"}`}>
            {loading ? <LoadingSpinnerIcon/> : `${children ?? dict.buttons.listing.contactToAuthor}`}
        </button>
    )
}

export default ChatLinkMessage;