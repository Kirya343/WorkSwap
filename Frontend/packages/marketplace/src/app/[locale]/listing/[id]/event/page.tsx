"use client"

import { useEffect, useState } from "react";
import NotFoundPage from "@core/pages/NotFoundPage";
import { useParams, useSearchParams } from "next/navigation";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { IEventPageRequest } from "@core/lib/listing/types";
import { IShortUser, IShortUserProfile } from "@core/lib/user/types";
import { useChats } from "@core/lib/chat/MessengerContext";
import { eventService } from "@core/lib/listing/eventService";
import { subscriptionService } from "@core/lib/listing/subscriptionService";
import ListingPageLayout from "../../../../../components/pages/listing/ListingPageLayout/ListingPageLayout";
import PriceTypes from "@core/components/common/PriceTypes/PriceTypes";
import RatingStars from "@core/components/common/RatingStars/RatingStars";
import FormattedDate from "@core/components/common/date/FormattedDate"
import Avatar from "@core/components/common/Avatar/Avatar";
import ChatWindow from "@/components/ui/chat/ChatWindow/ChatWindow";
import styles from "./EventPage.module.scss"
import ListingDetail from "@/components/pages/listing/ListingDetail/ListingDetail";
import SidebarCard from "@/components/pages/listing/SidebarCard/SidebarCard";
import ListingPageElement from "@/components/pages/listing/ListingPageElement/ListingPageElement";
import ListingAction from "@/components/pages/listing/ListingAction/ListingAction";

const EventPage = () => {

    const { id } = useParams();
    const eventId = Number(id);
    const searchParams = useSearchParams();
    const token: string | null = searchParams.get("token");
    const { dict } = useI18n();

    const {user} = useAuth();

    const [listingPage, setListingPage] = useState<IEventPageRequest | null>(null);
    const [author, setAuthor] = useState<IShortUserProfile | null>(null);
    const isOwner = !!(user?.sub == author?.sub);
    const [subscribed, setSubscribed] = useState<boolean>(false);
    const [participantsCount, setParticipantsCount] = useState<number>(0)
    const [participants, setParticipants] = useState<IShortUser[] | null>(null)
    const [isParticipant, setParticipant] = useState<boolean>(false);
    const { currentChatId, setCurrentChatId } = useChats();
    const [error, setError] = useState<boolean>(false);

    useEffect(() => {
        setCurrentChatId(null)

        async function loadEventPage() {
            if (!eventId) return
            const listing: IEventPageRequest = await eventService.getEventPage(eventId, token)
            if (!listing) setError(true);
            setListingPage(listing)
            setParticipantsCount(listing.participantsCount || 0);
            setParticipants(listing.participants)
            setCurrentChatId(listing.chat?.id || null)
            setAuthor(listing.author)
        }

        loadEventPage();

        subscriptionService.check(eventId, 'EVENT').then(setSubscribed)
        eventService.checkEventParticipant(eventId).then(setParticipant)

    }, [eventId, setCurrentChatId, token]);

    useEffect(() => {
        if (!currentChatId && listingPage?.chat?.id) setCurrentChatId(listingPage.chat.id)
    }, [currentChatId, listingPage, setCurrentChatId])

    const toggleParticipation = async () => {
        setParticipant(!isParticipant); // мгновенный отклик
        if (isParticipant) {
            eventService.removeEventParticipant(eventId)
                .then(() => setParticipantsCount(prev => prev - 1))
                .catch(() => {
                    setParticipant(true);
                    setParticipantsCount(prev => prev);
                })
        } else {
            eventService.addEventParticipant(eventId)
                .then(() => setParticipantsCount(prev => prev + 1))
                .catch(() => {
                    setParticipant(false);
                    setParticipantsCount(prev => prev);
                })
        }
    }

    if (error) return <NotFoundPage/>;
    
    return listingPage && (
        <ListingPageLayout
            listingPage={listingPage}
            author={author}
            listingActions={!isOwner && (
                <ListingAction 
                    onClick={() => 
                        subscriptionService.toggle(
                            eventId, setSubscribed, subscribed, 'EVENT', null
                        )}>
                    <span>{subscribed ? "Отписаться" : "Подписаться"}</span>
                </ListingAction>
            )}
            details={(
                <>
                    <ListingDetail title={dict.common.labels.event.price} customValue>
                        <PriceTypes listing={listingPage.listing} />
                    </ListingDetail>

                    <ListingDetail title={dict.common.labels.event.date}>
                        <FormattedDate isoDate={listingPage?.event.eventDate || ""} format="DMHM"/>
                    </ListingDetail>

                    <ListingDetail title={dict.common.labels.location}>
                        {listingPage?.listing.location || ""}
                    </ListingDetail>

                    <ListingDetail title={dict.common.labels.rating} customValue>
                        <RatingStars rating={listingPage?.listing.rating ?? 0}/>
                    </ListingDetail>
                </>
            )}
            extraSidebarNode={(
                <>
                    {participants && (
                        <SidebarCard>
                            <h3>{dict.common.labels.event.participants}</h3>
                            <div className={styles.participants}>
                                {participants.map((participant) => (
                                    <div 
                                        key={participant.name}
                                        className={styles.participant}
                                    >
                                        <Avatar 
                                            user={participant}
                                            size={50}
                                            link={false}
                                        />
                                        <span>{participant.name}</span>
                                    </div>
                                ))}
                            </div>
                        </SidebarCard>
                    )}
                    
                    <SidebarCard>
                        <ListingDetail title={dict.common.labels.event.participants}>
                            {participantsCount}{listingPage.event?.maxParticipants ? " / " + listingPage.event.maxParticipants : ""}
                        </ListingDetail>
                        <div 
                            className="btn btn-primary"
                            onClick={toggleParticipation}
                        >
                            {!isParticipant ? (
                                <span>{dict.buttons.event.participation.join}</span>
                            ) : (
                                <span>{dict.buttons.event.participation.leave}</span>
                            )}
                        </div>
                    </SidebarCard>
                </>
            )}
            extraContent={
                <ListingPageElement>
                    <ChatWindow title={listingPage.listing.localizedTitle} className={styles.chat} isMobileStatic/>
                </ListingPageElement>
            }
        />
    );
};

export default EventPage;