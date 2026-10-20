"use client"

import { useEffect, useState } from "react";
import NotificationsContainer from "./NotificationsConteiner/NotificationsContainer";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { usePathname } from "next/navigation";
import BellIcon from "@core/components/common/icons/BellIcon";
import UnreadNotifications from "./UnreadNotifications/UnreadNotifications";

const NotificationMobileButton = ({className}: {className: string}) => {
 
    const { dict } = useI18n();
    const { unreadNotificationsCount } = useNotification();
    const pathname = usePathname();
    const [isOpen, setOpen] = useState(false);

    useEffect(() => {
        setOpen(false);
    }, [pathname]);

    return (
        <>
            <button 
                className={className} 
                onClick={() => setOpen(!isOpen)}
                id="notificationAnchor"
            >
                <BellIcon />
                <span>{dict.buttons.menu.notifications}</span>
                {unreadNotificationsCount > 0 && (
                    <UnreadNotifications count={unreadNotificationsCount}/>
                )}
            </button>

            <NotificationsContainer
                isOpen={isOpen} 
                onClose={() => setOpen(false)}
                mobile 
            />
        </>
    );
};

export default NotificationMobileButton;