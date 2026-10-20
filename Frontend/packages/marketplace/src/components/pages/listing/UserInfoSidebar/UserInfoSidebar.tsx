"use client"

import { useNotification } from "@core/lib/notification/NotificationContext";
import { useAuth } from "@core/lib/auth/AuthContext";
import { IShortUserProfile } from "@core/lib/user/types";
import Avatar from "@core/components/common/Avatar/Avatar";
import PhoneIcon from '@core/components/common/icons/contacts/PhoneIcon';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import ChatLinkMessage from "@/components/ui/chat/ChatLinkMessage/ChatLinkMessage";
import styles from "./UserInfoSidebar.module.scss"
import { AUTH_BASE } from "@core/config";
import EnvelopeIcon from "@core/components/common/icons/contacts/EnvelopeIcon";

const UserInfoSidebar = ({listingId, author}: {listingId: number | null, author: IShortUserProfile | null}) => { 

    const { dict } = useI18n();
    const {notificate} = useNotification();
    const {user, isAuthenticated} = useAuth();

    if (!author) return null;

    const isOwner = !!(user?.sub == author.sub);

    return (
        <aside className={styles.sidebar}>
            {author.name && (
                <div className={`${styles.sellerCard} fade-down`}>
                    <div className={styles.sellerInfo}>
                        <Avatar
                            user={author}
                            size={100}
                            className={styles.avatar}
                        />
                        <div className={styles.meta}>
                            <h3>{author.name}</h3>
                            <div className={styles.rating}>
                                <span>{dict.common.labels.rating}: </span>
                                <span>{author.rating ?? 0} ★</span>
                            </div>
                            <div className={styles.actions}>
                                {isAuthenticated ? (
                                    <>  
                                        {!isOwner && (
                                            <ChatLinkMessage listingId={listingId} interlocutorSub={author.sub}>
                                                {dict.buttons.listing.contactToAuthor}
                                            </ChatLinkMessage>
                                        )}
                                    </>
                                ) : (
                                    <a
                                        href={`${AUTH_BASE}/auth?redirect=${encodeURIComponent(window.location.origin + window.location.href)}`}
                                        className="btn btn-primary"
                                    >
                                        {dict.buttons.loginToWrite}
                                    </a>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {author.name && (
                <div className={`${styles.contactCard} fade-down`}>
                    <h3>{dict.common.labels.contacts}</h3>
                    <div className={styles.methods}>

                        {author.phone && (
                            <div 
                                className={`${styles.contact} hover`}
                                onClick={() => {
                                    navigator.clipboard.writeText(author.phone ?? "")
                                        .then(() => notificate(dict.messages.notification.success.copyPhone, "success"))
                                        .catch(() => notificate("Ошибка", "error"));
                                }}
                            >
                                <PhoneIcon />
                                <span className={styles.value}>{author.phone}</span>
                            </div>
                        )}

                        {author.email && (
                            <div 
                                className={`${styles.contact} hover`}
                                onClick={() => {
                                    navigator.clipboard.writeText(author.email ?? "")
                                        .then(() => notificate(dict.messages.notification.success.copyEmail, "success"))
                                        .catch(() => notificate("Ошибка", "error"));
                                }}
                            >
                                <EnvelopeIcon />
                                <span className={`${styles.value} ellipsis`}>{author.email}</span>
                            </div>
                        )}

                        {(author.phone == null && author.email == null) && (
                            <span>Нет контактов</span>
                        )}
                    </div>
                </div>
            )}
        </aside>
    );
};

export default UserInfoSidebar;