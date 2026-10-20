"use client"

import { usePathname } from "next/navigation";
import NotificationMobileButton from '@core/components/ui/notifications/NotificationMobileButton';
import { createPortal } from "react-dom";
import { useEffect, useState } from "react";
import { useSwipeable } from 'react-swipeable';
import { AUTH_BASE } from "@core/config";
import { useNotification } from "@core/lib/notification/NotificationContext"
import LanguageSwitcher from '@core/components/layout/LanguageSwitcher/LanguageSwitcher';
import Avatar from "@core/components/common/Avatar/Avatar"
import AccountSidebarLinks from "@/components/pages/account/AccountSidebar/AccountSidebarLinks";
import ContactModal from "@/components/ui/ContactModal/ContactModal";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./MobuleMenu.module.scss"
import SignOutIcon from "@core/components/common/icons/SignOutIcon";
import UnreadNotifications from "@core/components/ui/notifications/UnreadNotifications/UnreadNotifications";
import BurgerIcon from "@core/components/common/icons/BurgerIcon"
import { useAuth } from "@core/lib/auth/AuthContext";
import SignInIcon from "@core/components/common/icons/SignInIcon";

const MobileMenu = () => {

    const { dict } = useI18n();
    const { user, isAuthenticated, logout } = useAuth();
    const [mobileMenuEm, setMobileMenuEm] = useState<HTMLElement | null>(null);
    const pathname = usePathname();
    const [isOpen, setOpen] = useState<boolean>(false);
    const [url, setUrl] = useState<string | null>(null);

    useEffect(() => {
        setUrl(window.location.href);
    }, []);

    const EDGE_SIZE = 120;
    const handlers = useSwipeable({
        onSwipedLeft: (eventData) => {
            const startX = eventData.initial[0];
            const screenWidth = window.innerWidth;

            const target = eventData.event?.target as HTMLElement | null;
            if (target?.closest('[data-mb-swipe-ignore]')) {
                return;
            }

            if (startX >= screenWidth - EDGE_SIZE && !isOpen) {
                setOpen(true);
            }
        },
        onSwipedRight: () => {
            if (isOpen) {
                setOpen(false);
            }
        },
        delta: 30,
        trackMouse: false,
        preventScrollOnSwipe: false,
    });

    useEffect(() => {
        setMobileMenuEm(document.getElementById("mobile-menu"));

        const cleanup = handlers.ref(document.body);
        return cleanup;
    }, []);

    useEffect(() => {
        setOpen(false);
    }, [pathname]);

    const { unreadNotificationsCount } = useNotification();

    return (
        <>
            <button 
                onClick={() => setOpen(p => !p)} 
                className={styles.navbarToggler} 
                id="notificationAnchor"
            >
                <BurgerIcon />
                {unreadNotificationsCount > 0 && (
                    <UnreadNotifications count={unreadNotificationsCount}/>
                )}
            </button>
            {mobileMenuEm && createPortal(
                <div className={`${styles.mobileMenu} ${isOpen ? styles.show : ""}`}>

                    <LanguageSwitcher mobile />

                    <div className={styles.userInfo}>
                        {isAuthenticated && (
                            <Avatar
                                user={user}
                                size={100}
                            />
                        )}

                        <h2>{user?.name}</h2>
                    </div>

                    {user?.name ? (
                        <button className={styles.navbarBtn} onClick={logout}>
                            <SignOutIcon />
                            <span>{dict.navigation.accountSidebar.logout}</span> 
                        </button>
                    ) : (
                        <a
                            href={`${AUTH_BASE}/auth${url ? `?redirect=${encodeURIComponent(url || "")}` : ""}`}
                            className={styles.navbarBtn}
                        >
                            <SignInIcon />
                            <span>{dict.navigation.accountSidebar.login}</span> 
                        </a>
                    )}

                    <div className={styles.accountManager}>
                        <AccountSidebarLinks />
                    </div>

                    <NotificationMobileButton className={styles.navbarBtn} />

                    <ContactModal/>
                </div>,
                mobileMenuEm
            )}
        </>
    )
};

export default MobileMenu;