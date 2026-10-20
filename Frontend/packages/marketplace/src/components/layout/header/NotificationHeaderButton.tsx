"use client"

import { useEffect, useState } from "react";
import * as Popover from "@radix-ui/react-popover";
import NotificationsContainer from "@core/components/ui/notifications/NotificationsConteiner/NotificationsContainer";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { usePathname } from "next/navigation";
import BellIcon from "@core/components/common/icons/BellIcon";
import UnreadNotifications from "@core/components/ui/notifications/UnreadNotifications/UnreadNotifications";

const NotificationHeaderButton = ({ className }: { className: string }) => {
    const { unreadNotificationsCount } = useNotification();
    const pathname = usePathname();
    const [isOpen, setOpen] = useState(false);

    useEffect(() => {
        setOpen(false);
    }, [pathname]);

    return (
        <Popover.Root
            open={isOpen}
            onOpenChange={setOpen}
        >
            <Popover.Anchor asChild>
                <button
                    className={className}
                    onClick={() => setOpen(!isOpen)}
                >
                    <BellIcon />

                    {unreadNotificationsCount > 0 && (
                        <UnreadNotifications count={unreadNotificationsCount} />
                    )}
                </button>
            </Popover.Anchor>

            <NotificationsContainer
                isOpen={isOpen}
                onClose={() => setOpen(false)}
            />
        </Popover.Root>
    );
};

export default NotificationHeaderButton;