"use client"

import { useMemo } from "react";
import PriceTypes from '@core/components/common/PriceTypes/PriceTypes';
import Link from 'next/link';
import { useChats } from '@core/lib/chat/MessengerContext';
import { ChatType } from '@core/lib/chat/constants/chatTypes';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import styles from "./HarizontalListingCard.module.scss"
import GearIcon from "@core/components/common/icons/GearIcon"
import UnreadNotifications from '@core/components/ui/notifications/UnreadNotifications/UnreadNotifications';
import { useRouter } from 'next/navigation';
import { IFullListing } from "@core/lib/listing/types";

const HarizontalListingCard = ({listing}: {listing: IFullListing}) => {
    const { dict } = useI18n()
    const { unreadMessages, chats } = useChats();
    const router = useRouter();

    const notifCount = useMemo(() => {
        const matchingChats = chats?.filter(chat => chat.targetId == listing.id && chat.type == ChatType.LISTING_DISCUSSION) ?? [];
        const matchingChatIds = new Set(matchingChats?.map(chat => chat.id));

        return unreadMessages?.filter(msg => matchingChatIds.has(msg.chatId)).length || 0;
    }, [chats, listing.id, unreadMessages])

    const navigator = () => {
        if (listing.type == "EVENT") {
            router.push(`/listing/${listing.id}/event`)
        } else {
            router.push(`/listing/${listing.id}`)
        }
    }

    return (
        <article className={styles.card} onClick={() => navigator()}>
            <img 
                src={listing.imagePath || "/images/placeholders/default-listing.svg"}
                onError={(e) => { e.currentTarget.src = "/images/placeholders/default-listing.svg"; }}
            />

            <div className={styles.body}>
                <h3 className={styles.title}>{listing.localizedTitle || dict.common.fallbacks.noTitle}</h3>
                <div className={styles.marks}>
                    {listing.temporary && <span className={styles.draftMark}>Черновик</span>}
                    {!listing.active && <span className={styles.hiddenMark}>Скрыто</span>}
                </div>
                <div className={styles.footer}>
                    <PriceTypes className={styles.price} listing={listing} />
                    <div className={styles.views}>
                        <span>{dict.common.labels.views}: </span>
                        <span>{listing.views}</span>
                    </div>
                </div>
                <div className={styles.actions}>
                    <Link
                        className="btn btn-primary"
                        href={`/account/listing/${listing.id}/manage`}
                        onClick={(e) => e.stopPropagation()}
                        id="notificationAnchor"
                    >
                        <GearIcon size={20}/>
                        {notifCount > 0 &&
                            <UnreadNotifications count={notifCount}/>
                        }
                    </Link>
                </div>
            </div>
        </article>
    );
}

export default HarizontalListingCard;