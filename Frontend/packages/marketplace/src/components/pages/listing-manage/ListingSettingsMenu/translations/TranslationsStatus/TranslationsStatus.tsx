import { IListingTranslation } from "@core/lib/listing/types";
import { useEffect, useState } from "react";
import styles from "./TranslationsStatus.module.scss"

interface TranslationsStatusProps {
    lang: string;
    translations: IListingTranslation | null;
}
const TranslationsStatus = ({lang, translations}: TranslationsStatusProps) => {

    const [progress, setProgress] = useState<number>(0);

    useEffect(() => {
        const t = translations?.[lang];
        if (t) {
            if (t.title && t.description) {
                setProgress(100);
            } else if (t.title || t.description) {
                setProgress(50);
            } else {
                setProgress(0);
            }
        }
    }, [lang, translations]);

    return (
        <div className={styles.translationStatus}>
            <div className={styles.progress}>
                <i className={`fa-regular fa-circle ${styles.first}`}></i>
                {progress > 0 && (
                    <i className={`fa-solid ${progress < 100 ? "fa-circle-half" : "fa-circle"} ${styles.second}`}></i>
                )}

                {progress === 100 && (
                    <i className={`fa-solid fa-check ${styles.third}`}></i>
                )}
            </div>
        </div>
    );
};

export default TranslationsStatus;