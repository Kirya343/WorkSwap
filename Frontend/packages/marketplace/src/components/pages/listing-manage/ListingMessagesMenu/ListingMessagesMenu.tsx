import { IFullListing } from "@core/lib/listing/types";
import { ChatType } from "@core/lib/chat/constants/chatTypes";
import styles from "./ListingMessagesMenu.module.scss"
import ChatsPage from "@/components/ui/chat/ChatsPage/ChatsPage";
import { useChats } from "@core/lib/chat/MessengerContext";
import { useEffect } from "react";

const ListingMessagesMenu = ({listing}: {listing: IFullListing}) => {

    const { setCurrentChatId } = useChats();

    useEffect(() => {
        setCurrentChatId(null);
    }, [])

    return (
        <div className={styles.layout}>
            <ChatsPage type={ChatType.LISTING_DISCUSSION} targetId={listing.id} />
        </div>
    );
}

export default ListingMessagesMenu;