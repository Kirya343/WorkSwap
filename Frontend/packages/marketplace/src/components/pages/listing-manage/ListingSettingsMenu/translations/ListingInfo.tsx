import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { IListingTranslation } from '@core/lib/listing/types';
import { useEffect, useState } from 'react';
import AutoTranslateModal from './AutoTranslateModal';

interface ListingInfoProps {
    currentLang: string;
    translations: IListingTranslation;
    updateTranslation: (lang: string, title: string, descriprion: string) => void;
    saveTranslations: () => void;
    listingId: number | null;
}
const ListingInfo = ({
    currentLang,
    translations,
    updateTranslation,
    saveTranslations,
    listingId
}: ListingInfoProps) => {

    const { dict } = useI18n();

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");

    const handleSave = () => {
        updateTranslation(currentLang, title, description);
        saveTranslations()
    }

    useEffect(() => {
        setTitle(translations[currentLang]?.title || "")
        setDescription(translations[currentLang]?.description || "")
    }, [currentLang, translations])

    return (
        <>
            <label className='form-group'>
                {dict.common.labels.title}{currentLang != 'undetected' && ` (${dict.common.languages[currentLang]})`}:
                <input
                    type="text"
                    value={title}
                    maxLength={250}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder={dict.common.placeholders.listing.title}  
                />
            </label>

            <label className='form-group' id="listingDescription">
                {dict.common.labels.description}{currentLang != 'undetected' && ` (${dict.common.languages[currentLang]})`}:
                <textarea
                    id="listingDescriptionTxt"
                    rows={4}
                    value={description}
                    maxLength={1900}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder={dict.common.placeholders.listing.description}
                />
            </label>

            <button type="button" className="btn btn-primary" onClick={handleSave}>
                {dict.buttons.listing.saveTranslation}
            </button>

            <AutoTranslateModal 
                translations={translations} 
                currentLang={currentLang} 
                updateTranslation={updateTranslation} 
                saveTranslations={saveTranslations}
                listingId={listingId} 
            />
        </>
    );
};

export default ListingInfo;