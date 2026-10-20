"use client"

import { useEffect, useState } from "react";
import { languagesList } from "@core/lib/common/constants/languages";
import Modal from "../ui/Modal/Modal";
import { usePathname, useRouter } from "next/navigation";
import { useI18n } from "@core/lib/common/contexts/I18nContext";

const LanguageSelectModal = () => {
    const [isOpen, setOpen] = useState(false)
    const pathname = usePathname();
    const router = useRouter();
    const { locale } = useI18n();

    const changeLanguage = (lng: string) => {
        if (lng === locale) return;

        document.cookie = `locale=${lng}; path=/; max-age=31536000`;

        const segments = pathname.split("/");
        segments[1] = lng; // заменяем locale

        const newPath = segments.join("/") || `/${lng}`;

        router.push(newPath);
    };

    useEffect(() => {
        const isLanguage = !!localStorage.getItem("i18nextLng")
        setOpen(!isLanguage);
    }, [])

    return (
        <Modal
            isOpen={isOpen}
            onClose={() => {}}
            title="Select Language"
        >
            {languagesList.map(({ code, label }) => (
                <button
                    key={code}
                    className="btn btn-primary"
                    onClick={() => changeLanguage(code)}
                >
                    <span>{label}</span>
                </button>
            ))}
        </Modal>
    );
};

export default LanguageSelectModal;