"use client"

import Avatar from "@core/components/common/Avatar/Avatar";
import RatingStars from "@core/components/common/RatingStars/RatingStars";
import { useAuth } from "@core/lib/auth/AuthContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { userService } from "@core/lib/user/services";
import { useEffect, useState } from "react";
import AccountSidebarLinks from "./AccountSidebarLinks";
import ContactModal from "@/components/ui/ContactModal/ContactModal";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./AccountSidebar.module.scss";

const AccountSidebar = () => {

    const { dict } = useI18n();
    const { user } = useAuth();
    const [telegramConnected, setTelegramConnected] = useState(true)
    const { notificate } = useNotification();

    async function connectTelegram() {
        try {
            const linkUrl: string = await userService.connectUserTelegram();

            if (linkUrl) {
                setTelegramConnected(true);
                window.open(linkUrl, '_blank'); // Открываем в новой вкладке
                notificate("Telegram успешно подключён, теперь вам будут приходить уведомления через это приложение!", "success")
            }
        } catch (error) {
            console.error('Ошибка запроса:', error);
            notificate("Ошибка подключения аккаунта Telegram", "error")
        }
    }

    useEffect(() => {
        async function checkTelegram() {
            const data = await userService.checkTelegramConnected();
            setTelegramConnected(data);
        }

        checkTelegram();
    }, []);

    return(
        <aside className={styles.accountSidebar}>
            <div className={styles.profileCard}>
                <Avatar
                    user={user}
                    size={100}
                    className={styles.avatar}
                />
                <h4 className={styles.name}>{user?.name}</h4>
                <div className={styles.rating}>
                    <RatingStars rating={user?.rating ?? 0} />
                    <span>({user?.rating})</span>
                </div>
                {/* <a href="/account/settings" className="btn btn-outline-primary btn-sm">{t(`accountSidebar.links.editProfile`, { ns: 'navigation' })}</a> */}
            </div>

            <AccountSidebarLinks/>

            <ContactModal/>

            {!telegramConnected && (
                <button onClick={() => connectTelegram()} className={styles.telegramBtn}>
                    {dict.buttons.special.connectTelegram}
                </button>
            )}
        </aside>
    );
};

export default AccountSidebar;