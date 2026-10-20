"use client"

import { useEffect, useState } from "react";
import { usePathname } from "next/navigation";
import { useNotification } from "@core/lib/notification/NotificationContext";
import Modal from "@core/components/ui/Modal/Modal"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./ContactModal.module.scss"
import LightbulbIcon from '@core/components/common/icons/LightbulbIcon'
import EnvelopeIcon from '@core/components/common/icons/contacts/EnvelopeIcon'
import TelegramIcon from '@core/components/common/icons/contacts/TelegramIcon'
import ChatLinkMessage from "../chat/ChatLinkMessage/ChatLinkMessage";

const ContactModal = () => {

    const {notificate} = useNotification();

    const [isOpen, setOpen] = useState<boolean>(false)

    const { dict } = useI18n()
    function toggleModal() {
        setOpen(!isOpen);
    }

    useEffect(() => {
        setOpen(false);
    }, [usePathname]);

    return (
        <>
            <div className={`${styles.openModal} hover`} onClick={() => toggleModal()}>
                <LightbulbIcon />
                {dict.buttons.menu.helpBeBetter}
            </div>

            <Modal
                isOpen={isOpen}
                onClose={toggleModal}
                title={dict.buttons.menu.contactToSupport}
            >
                <ChatLinkMessage listingId={null} interlocutorSub={"0JPvHQ8Ea4RrnFgUHjg5"}>
                    {dict.buttons.listing.contactToAuthor}
                </ChatLinkMessage>
                
                <div 
                    className={`${styles.contactBtn} hover`}
                    onClick={() => {
                        navigator.clipboard.writeText("workswap.org@gmail.com")
                            .then(() => notificate(dict.messages.notification.success.copyEmail, "success"))
                            .catch(() => notificate("Ошибка", "error"));
                    }}
                >
                    <EnvelopeIcon />
                    workswap.org@gmail.com
                </div>

                <div 
                    className={`${styles.contactBtn} hover`}
                    onClick={() => {
                        navigator.clipboard.writeText('@workswap_official')
                            .then(() => notificate(dict.messages.notification.success.copyTelegramTag, "success"))
                            .catch(() => notificate("Ошибка", "error"));
                    }} 
                >
                    <TelegramIcon />
                    @workswap_official
                </div>
            </Modal>
        </>
    );
};

export default ContactModal;