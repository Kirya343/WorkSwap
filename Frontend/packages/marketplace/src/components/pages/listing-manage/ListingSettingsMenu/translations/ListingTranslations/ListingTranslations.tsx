import { use, useCallback, useEffect, useMemo, useState } from "react";
import ListingInfo from "../ListingInfo";
import TranslationsStatus from "../TranslationsStatus/TranslationsStatus";
import { supportedLanguages } from '@core/lib/common/constants/languages';
import styles from "./ListingTranslations.module.scss"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { IListingTranslation } from "@core/lib/listing/types";
import { listingService } from "@core/lib/listing/services"

const ListingTranslations = ({ id }: {id: number | null}) => {

    const { dict } = useI18n();

    const { notificate } = useNotification();

    const [initialized, setInitialized] = useState(false);

    const [loading, setLoading] = useState(true);
    const [translations, setTranslations] = useState<IListingTranslation | null>(null);
    const [currentLang, setCurrentLang] = useState("undetected");
    const [langs, setLangs] = useState<string[]>([]);
    const unusedLanguages = useMemo<string[]>(() => {
        return supportedLanguages.filter((l: string) => !langs.includes(l)) ?? supportedLanguages
    }, [langs])

    const updateTranslation = useCallback((lang: string, title: string, description: string) => {
        var key: string;
        if (lang) {
            key = lang;
        } else {
            key = 'undetected';
        }

        setTranslations(prev => ({
            ...prev,
            [key]: { title, description },
        }));
    }, []);

    const saveTranslations = useCallback(async () => {

        console.log(id, translations)
        if (!id || !translations) return;

        try {
            const data = await listingService.modifyTranslations(id, translations);
            setLangs(data)
        } catch {
            notificate(dict.messages.notification.error.listingUpdate, "error")
        }
    }, [id, translations]);

    useEffect(() => {
        if (!id) return
        listingService.getTranslations(id).then((data: IListingTranslation) => {
            setTranslations(data);
            const firstLang = Object.keys(data)[0];
            setLangs(Object.keys(data))
            if (firstLang) setCurrentLang(firstLang)
            setLoading(false);
            setInitialized(true)
        })
    }, [id]);

    return (
        <div className={styles.translationEditor}>
            {langs.length > 0 && (
                <div className={styles.langCards}>
                    {langs.map((lang) => (
                        <div 
                            key={lang}
                            className={`${styles.lang} hover ${currentLang == lang ? styles.active : ""}`} 
                            onClick={() => setCurrentLang(lang)}
                        >
                            <span>{dict.common.languages[lang]}</span>
                            <TranslationsStatus lang={lang} translations={translations}/>
                        </div>
                    ))}
                    {unusedLanguages.length > 0 && (
                        <div 
                            className={`${styles.lang} hover ${currentLang == "undetected" ? styles.active : ""}`} 
                            onClick={() => setCurrentLang("undetected")}
                        >
                            <span>+ Добавить язык</span>
                        </div>
                    )}
                </div>
            )}

            {currentLang == "undetected" && (
                <div className={styles.languageSelector}>
                    {unusedLanguages.map(l => (
                            <button key={l} onClick={() => setCurrentLang(l)}>{dict.common.languages[l]}</button>
                        ))
                    }
                </div>
            )}

            {!loading && (
                <ListingInfo
                    currentLang={currentLang}
                    translations={translations || {}}
                    updateTranslation={updateTranslation}
                    saveTranslations={saveTranslations}
                    listingId={id}
                />
            )}            
        </div>
    );
}

export default ListingTranslations;