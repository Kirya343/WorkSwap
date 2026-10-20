"use client"

import { useRef, useEffect, useState, useMemo, useLayoutEffect } from "react";
import SendMessageArea from "../SendMessageArea/SendMessageArea";
import MessagesGroup from "../MessagesGroup/MessagesGroup";
import { createPortal } from "react-dom";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useChats } from "@core/lib/chat/MessengerContext";
import { IShortUser } from "@core/lib/user/types";
import { ChatType, privateChatTypes } from "@core/lib/chat/constants/chatTypes"
import Avatar from "@core/components/common/Avatar/Avatar";
import Link from "next/link";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./ChatWindow.module.scss"
import sharedStyles from "../ChatShared.module.scss"
import ArrowIcon from "@core/components/common/icons/ArrowIcon";
import CardsIcon from "@core/components/common/icons/CardsIcon";
import UserIcon from "@core/components/common/icons/UserIcon";

const ChatWindow = ({title, className, isMobileStatic}: {title?: string, className?: string, isMobileStatic?: boolean}) => {

    const { dict } = useI18n();
    const { user } = useAuth();

    const { messages, setChatListingVisible, currentChatId, setCurrentChatId, currentChat } = useChats();

    const chatInterlocutor = useMemo<IShortUser | null>(
        () => currentChat?.interlocutors?.find(i => i.sub != user?.sub) ?? null, [currentChat]);

    const messagesContainer = useRef<HTMLDivElement  | null>(null);
    const [isMobile, setIsMobile] = useState(false);
    const [modalRoot, setModalRoot] = useState<HTMLElement | null>(null);

    useEffect(() => {
        const check = () => setIsMobile(window.innerWidth <= 600);

        check();
        window.addEventListener("resize", check);

        setModalRoot(document.getElementById("modal-root"));

        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape") {
                setCurrentChatId(null);
            }
        });

        return () => window.removeEventListener("resize", check);
    }, []);

    useLayoutEffect(() => {
        const el = messagesContainer.current;

        if (!el) return;

        el.scrollTop = el.scrollHeight;
    }, [messages]);

    function renderChatWindow() {
        return (
            <div 
                id={currentChat?.type} 
                className={`${className ?? ""} ${styles.chatWindow} ${currentChatId ? styles.show : ""}`}
            >
                {currentChatId ? (
                    <>
                        <div className={styles.chatHeader}>
                            <div className={styles.chatInfo}>
                                <button
                                    onClick={() => setCurrentChatId(null)} 
                                    className={styles.mobileDialogsToggle}
                                >
                                    <ArrowIcon left />
                                </button>

                                {currentChat?.type !== undefined &&
                                    privateChatTypes.includes(currentChat.type) && (
                                    <>
                                        {currentChat?.type === ChatType.LISTING_DISCUSSION && currentChat.listing ? (
                                            <div className={sharedStyles.dialogAvatar}>
                                                <img className={sharedStyles.listingImg} src={currentChat?.listing?.imagePath} />
                                                <Avatar user={chatInterlocutor} size={40} className={sharedStyles.userAvatar} link={false} />
                                            </div>
                                        ) : (
                                            <Avatar user={chatInterlocutor} size={50} link={false} />
                                        )}
                                    </>
                                )}
                                <h4 id="chatTitle">{title ?? chatInterlocutor?.name}</h4>
                            </div>
                            <div className={styles.mobileActions}>
                                {chatInterlocutor?.sub && (
                                    <button 
                                        className={`${styles.action} btn btn-primary btn-sm`}
                                        onClick={() => setChatListingVisible(prev => !prev)}
                                    >
                                        <CardsIcon />
                                    </button>
                                )}
                                <Link
                                    href={`/profile/${chatInterlocutor?.sub}`} 
                                    className={`${styles.action} btn btn-outline-primary btn-sm`}
                                >
                                    <UserIcon />
                                </Link>
                            </div>
                            <div className={styles.actions}>
                                {currentChat?.listing?.id && (
                                    <button 
                                        className="btn btn-primary btn-sm" 
                                        onClick={() => setChatListingVisible(prev => !prev)}
                                    >{dict.buttons.messenger.listing}</button>
                                )}
                                {chatInterlocutor?.sub && (
                                    <Link 
                                        href={`/profile/${chatInterlocutor?.sub}`} 
                                        className="btn btn-outline-primary btn-sm"
                                    >
                                        {dict.buttons.messenger.profile}
                                    </Link>
                                )}
                                
                            </div>
                        </div>

                        <div 
                            className={styles.messagesContainer} 
                            ref={messagesContainer}
                        >
                            {/* {error && (
                                <div className="web-socket-connection-status">
                                    <span>{t(`messenger.connectionLost`, { ns: 'errors' })}</span>
                                    <br/>
                                    <i className="fa-solid fa-spinner-third fa-spin"></i>
                                </div>
                            )} */}

                            {(messages?.length === 0) && (
                                <p>{dict.common.fallbacks.noMessages}</p>
                            )}

                            {messages?.map((group) => (
                                <MessagesGroup group={group} key={group.id} />
                            ))}
                        </div>

                        <SendMessageArea/>
                    </>
                ) : (
                    <div className={styles.noSelectedChat}>
                        <h2>Чат не выбран</h2>
                        <span className={styles.subTitle}>Выберите чат из списка, чтобы начать общение</span>
                    </div>
                )}
            </div>
        );
    }

    if (!modalRoot) return null;

    return isMobile && isMobileStatic ? createPortal(renderChatWindow(), modalRoot) : renderChatWindow()
};

export default ChatWindow;