"use client"

import { useEffect, useMemo, useState } from 'react';
import { useChats } from '@core/lib/chat/MessengerContext';
import { ChatType } from '@core/lib/chat/constants/chatTypes';
import { listingService } from '@core/lib/listing/services';
import SidebarSectionLayout from '@core/components/layout/SidebarSectionLayout/SidebarSectionLayout'
import { useParams } from 'next/navigation';
import ListingSettingsMenu from '../../../../../../components/pages/listing-manage/ListingSettingsMenu/ListingSettingsMenu';
import ListingAnalyticMenu from '../../../../../../components/pages/listing-manage/ListingAnalyticMenu';
import ListingMessagesMenu from '../../../../../../components/pages/listing-manage/ListingMessagesMenu/ListingMessagesMenu';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { useChatSubscription } from '@core/lib/chat/hooks/useChatSubscription';
import { useChatsLoad } from '@core/lib/chat/hooks/useChatsLoad';
import AccountHeader from '@/components/pages/account/AccountHeader/AccountHeader';

const ListingMenu = {
    SETTINGS: {
        first: true,
        name: "Settings",
        icon: <></>
    },
    ANALYTICS: {
        first: false,
        name: "Analytics",
        icon: <></>
    },
    MESSAGES: {
        first: false,
        name: "Messages",
        icon: <></>
    }
} as const;

const ListingManagePage = () => {

    useChatSubscription();
    useChatsLoad();

    const { dict } = useI18n();
    const { id } = useParams();
    const numberId = useMemo<number>(() => Number(id), [id]);
    
    const [listing, setListing] = useState(null);

    const { unreadMessages, chats } = useChats();

    const notifCount = useMemo<number>(() => {
        const matchingChats = chats?.filter(chat => chat.targetId == numberId && chat.type == ChatType.LISTING_DISCUSSION) || [];
        const matchingChatIds = new Set(matchingChats.map(chat => chat.id));

        return unreadMessages?.filter(msg => matchingChatIds.has(msg.chatId)).length || 0;
    }, [chats, numberId, unreadMessages])

    useEffect(() => {
    
        async function loadListing(listingId: number) {
            const data = await listingService.getById(listingId);
            setListing(data);
        }

        if (numberId) {
            loadListing(Number(numberId));
        }
    }, [numberId]);

    return (
        <>
            <AccountHeader backLink={'/account/my-listings'} title={dict.common.titles.listingManage}/>

            {listing && (
                <SidebarSectionLayout
                    pageName="listingManage"
                    sections={ListingMenu}
                    notifications={{menu: ListingMenu.MESSAGES, count: notifCount}}
                    rowMode
                >
                    {(currentSection) => (
                        currentSection === ListingMenu.SETTINGS ? <ListingSettingsMenu listing={listing} /> :
                        currentSection === ListingMenu.ANALYTICS ? <ListingAnalyticMenu listing={listing} /> :
                        currentSection === ListingMenu.MESSAGES ? <ListingMessagesMenu listing={listing} /> :
                        null
                    )}
                </SidebarSectionLayout>
            )}
        </>
    );
};

export default ListingManagePage;