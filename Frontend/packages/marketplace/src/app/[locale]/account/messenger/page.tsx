"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useChatsLoad } from "@core/lib/chat/hooks/useChatsLoad";
import { useChatSubscription } from "@core/lib/chat/hooks/useChatSubscription";
import AccountHeader from "@/components/pages/account/AccountHeader/AccountHeader";
import ChatsPage from "@/components/ui/chat/ChatsPage/ChatsPage";
import { useEffect } from "react";

const MessengerPage = () => {

    useChatSubscription();
    useChatsLoad();

    const { dict } = useI18n();

    useEffect(() => {
        const viewport = window.visualViewport;

        if (!viewport) {
            return;
        }

        const updateViewport = () => {
            document.documentElement.style.setProperty(
                "--visual-viewport-height",
                `${viewport.height}px`
            );

            document.documentElement.style.setProperty(
                "--visual-viewport-offset-top",
                `${viewport.offsetTop}px`
            );
        };

        updateViewport();

        viewport.addEventListener("resize", updateViewport);
        viewport.addEventListener("scroll", updateViewport);

        return () => {
            viewport.removeEventListener("resize", updateViewport);
            viewport.removeEventListener("scroll", updateViewport);
        };
    }, []);

    return (
        <>
            <AccountHeader title={dict.common.titles.messenger} />

            <ChatsPage />
        </>
    );
};

export default MessengerPage;