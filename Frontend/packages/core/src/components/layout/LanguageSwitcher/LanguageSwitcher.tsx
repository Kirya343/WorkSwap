"use client"

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import { languagesList } from "@core/lib/common/constants/languages";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./LanguageSwitcher.module.scss"

const LanguageSwitcher = ({mobile}: {mobile?: boolean}) => {
    const { locale } = useI18n();
    const pathname = usePathname();
    const router = useRouter();

    const [isOpen, setOpen] = useState(false);

    function toggleLangMenu() {
        setOpen(prev => !prev);
    }

    useEffect(() => {
        setOpen(false);
    }, [pathname]);

    const changeLanguage = (lng: string) => {
        if (lng === locale) return;

        document.cookie = `locale=${lng}; path=/; max-age=31536000`;

        const segments = pathname.split("/");
        segments[1] = lng; // заменяем locale

        const newPath = segments.join("/") || `/${lng}`;

        router.push(newPath);
    };

    return (
        <div className={`${styles.toggler} ${mobile ? styles.mobile : ""}`}>
            <div
                className={`${styles.button} ${isOpen ? styles.active : ""}`}
                onClick={toggleLangMenu}
            >
                <img src={`/images/flags/${locale}.png`} alt="RU" className={styles.flag}/>
            </div>

            <div className={`${styles.dropdown} ${isOpen ? styles.active : ""}`}>
                {languagesList.map(({ code, label }) => (
                    <button
                        key={code}
                        type="button"
                        className={`${styles.langItem} ${
                            locale === code ? styles.active : ""
                        }`}
                        onClick={() => changeLanguage(code)}
                    >
                        <img src={`/images/flags/${code}.png`} alt="RU" className={styles.flag}/>
                        <span>{label}</span>
                    </button>
                ))}
            </div>
        </div>
    );
};


export default LanguageSwitcher;