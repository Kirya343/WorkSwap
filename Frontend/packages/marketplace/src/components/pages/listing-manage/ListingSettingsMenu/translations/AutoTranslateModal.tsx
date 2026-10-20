import Modal from "@core/components/ui/Modal/Modal"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { listingService } from "@core/lib/listing/services"
import { IListingTranslation } from "@core/lib/listing/types"
import { useNotification } from "@core/lib/notification/NotificationContext";
import clsx from "clsx";
import { Dispatch, SetStateAction, useState } from "react"

interface AutoTranslateModalProps {
    translations: IListingTranslation | null;
    listingId: number | null;
    currentLang: string;
    updateTranslation: (lang: string, title: string, descriprion: string) => void;
    saveTranslations: () => void;
}

const AutoTranslateModal = ({ 
    translations,
    listingId,
    currentLang,
    updateTranslation
}: AutoTranslateModalProps) => {

    const [isOpen, setOpen] = useState<boolean>(false);
    const langs = Object.keys(translations || []);
    const { dict } = useI18n();
    const { notificate } = useNotification();
    const [preferedRefLang, setPreferedRefLang] = useState<string>(langs[0])

    const handleAutoTranslate = async (lang: string) => {

        if (!listingId) return;

        try {
            const data = await listingService.autoTranslate(listingId, lang, preferedRefLang);
            updateTranslation(lang, data.title, data.description);
            setOpen(false)
            console.log(data)
        } catch {
            notificate(dict.messages.notification.error.listingUpdate, "error")
        }
    }

    return (
        <>
            <button 
                type="button" 
                className="btn btn-primary"
                onClick={() => setOpen(true)}
            >
                Перевести автоматически
            </button>

            <Modal isOpen={isOpen} onClose={() => setOpen(false)} title="Автоматический перевод">
                <h3>Какой перевод объявления использовать в качестве оригинала?</h3>
                <div 
                    style={{ 
                        display: "flex", 
                        gap: "0.5rem",
                        width: "100%",
                        justifyContent: "center"
                    }}
                >
                    {langs.map(lang => 
                        <button 
                            key={lang}
                            onClick={() => setPreferedRefLang(lang)} 
                            className={clsx("btn", lang == preferedRefLang ? "btn-outline-primary" : "btn-primary")}
                        >
                            {lang}
                        </button>
                    )}
                </div>
                <button 
                    className="btn btn-primary" 
                    onClick={() => handleAutoTranslate(currentLang)}
                >
                    Перевести
                </button>
            </Modal>
        </>
    )
}

export default AutoTranslateModal;