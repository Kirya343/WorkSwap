import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { IFullUser } from '@core/lib/user/types';
import { useMemo } from 'react';
import SettingSection from '../SettingSection/SettingSection';
import SliderCheckbox from "@core/components/common/checkbox/SliderCheckbox/SliderCheckbox"

interface SettingsProps {
    user: IFullUser;
    updateUser: (updates: Partial<IFullUser>) => void;
}
const PrivacySettings = ({ user, updateUser }: SettingsProps) => {

    const { dict } = useI18n()

    const phoneVisible = useMemo(() => {return user.phoneVisible || false}, [user.phoneVisible]);
    const emailVisible = useMemo(() => {return user.emailVisible || false}, [user.emailVisible]);

    return (
        <SettingSection
            title={dict.common.settings.labels.privacy}
        >
            <div className="form-group">
                <label>{dict.tooltips.settings.privacy}</label>
                <SliderCheckbox
                    id="phoneVisibility"
                    checked={emailVisible}
                    onChange={(e) => updateUser({ phoneVisible: e.target.checked })}
                    options={dict.tooltips.settings.phoneVisibility}
                />
            </div>

            <div className="form-group">
                <SliderCheckbox
                    id="emailVisibility"
                    checked={emailVisible}
                    onChange={(e) => updateUser({ emailVisible: e.target.checked })}
                    options={dict.tooltips.settings.emailVisibility}
                />
            </div>
        </SettingSection>
    );
};

export default PrivacySettings;