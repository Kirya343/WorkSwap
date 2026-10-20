import { useMemo } from 'react';
import { IFullUser } from '@core/lib/user/types';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import LocationSelector from '@/components/ui/selectors/LocationSelector';
import SettingSection from '../SettingSection/SettingSection';
import styles from "./PreferencesSettings.module.scss"
import ThemeChanger from '@core/components/layout/ThemeChanger';

interface PreferencesSettingsProps {
    user: IFullUser;
    updateUser: (updates: Partial<IFullUser>) => void;
}
const PreferencesSettings = ({ user, updateUser }: PreferencesSettingsProps) => {

    const { dict } = useI18n();

    const languages = useMemo(() => {
        return user.languages || [];
    }, [user.languages]);

    // Языки (переключение кнопок)
    const toggleLanguage = (lang: string) => {
        updateUser({ languages: languages.includes(lang) ? languages.filter((l) => l !== lang) : [...languages, lang] })
    };

    return (
        <>
            {/* Языки */}
            <SettingSection
                title={dict.common.settings.labels.myLanguages}
                subtitle={dict.tooltips.settings.myLanguages}
            >
                <div className="form-group flex-row">
                    {["ru", "fi", "en", "it"].map((lang) => (
                        <button
                            key={lang}
                            type="button"
                            className={`${styles.langSelectBtn} ${languages.includes(lang) ? "active" : ""}`}
                            onClick={() => toggleLanguage(lang)}
                        >
                            {dict.common.languages[lang]}
                        </button>
                    ))}
                </div>
            </SettingSection>

            {/* Местоположение */}
            <SettingSection
                title={dict.common.settings.labels.myLocation}
                subtitle={dict.tooltips.settings.myLocation}
            >
                <LocationSelector locationId={user.locationId} onChange={(locationId) => updateUser({ locationId: locationId })} />
            </SettingSection>

            <SettingSection
                title={dict.common.settings.labels.theme}
            >
                <ThemeChanger id='settingsThemeChanger'/>
            </SettingSection>
        </>
    );
};

export default PreferencesSettings;