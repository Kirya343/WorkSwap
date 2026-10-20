import { useI18n } from '@core/lib/common/contexts/I18nContext';
import { useNotification } from '@core/lib/notification/NotificationContext';

import { IFullUser } from '@core/lib/user/types';
import { useEffect, useMemo, useState } from 'react';
import SettingSection from '../SettingSection/SettingSection';
import styles from "./ProfileSettings.module.scss"

interface SettingsProps {
    user: IFullUser;
    updateUser: (updates: Partial<IFullUser>) => void;
}
const ProfileSettings = ({ user, updateUser }: SettingsProps) => {

    const name = useMemo(() => {return user.name || ""}, [user.name]);
    const email = useMemo(() => {return user.email || ""}, [user.email]);
    const phone = useMemo(() => {return user.phone || ""}, [user.phone]);
    const bio = useMemo(() => {return user.bio || ""}, [user.bio]);
    const avatarType = useMemo(() => {return user.avatarType || "uploaded"}, [user.avatarType]);

    // Подсчёт символов
    const maxNameLen = 30;
    const maxPhoneLen = 16;
    const maxBioLen = 1900;

    const { dict } = useI18n()
    const {notificate} = useNotification();

    const [uploadedAvatar, setUploadedAvatar] = useState<string | null>(null);

    useEffect(() => {
        if(user) setUploadedAvatar(user?.uploadedAvatar)
    }, [user])

    // Валидация телефона
    function validatePhone(value: string) {
        let val = value;
        if (val.indexOf("+") > 0) val = val.replace(/\+/g, "");
        val = val.replace(/[^0-9+]/g, "");
        if (val.indexOf("+") > 0) val = val.replace(/\D/g, "");
        if ((val.match(/\+/g) || []).length > 1) {
            const parts = val.split("+");
            val = "+" + parts.slice(1).join("");
        }
        return val;
    }

    const uploadtoCloud = async (file: File) => {
        try {
            
            // const formData = new FormData();
            // formData.append("image", file);

            // const imageUrl = await uploadService.uploadAvatar(formData);

            // if (imageUrl) {
            //     setUploadedAvatar(imageUrl)
            //     notificate("Успешно", "success");
            // } else {
            //     return;
            // }
        } catch (error) {
            console.error("Ошибка загрузки файла:", error);
            notificate("Ошибка загрузки изображения", "error");
        }
    };

    const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!e.target.files) return;
        const file = e.target.files[0];
        if (!file) {
            console.log("[handleImageUpload] Файл не выбран, но чёта пытается грузить");
            return;
        }
        await uploadtoCloud(file);
        e.target.value = "";
    };

    return (
        <>
            {/* Имя, Email, Телефон */}
            <SettingSection title={dict.common.settings.labels.baseInfo}>
                <div className="form-group">
                    <label>{dict.common.labels.name}</label>
                    <div className="input-wrapper">
                        <input
                            type="text"
                            value={name ?? ""}
                            onChange={(e) => updateUser({ name: e.target.value })}
                            maxLength={maxNameLen}
                            required
                        />
                        <span className="char-counter">{name?.length} / {maxNameLen}</span>
                    </div>
                </div>

                <div className="form-group">
                    <label>Email</label>
                    <input
                        type="email"
                        value={email ?? ""}
                        /* onChange={(e) => emailChange(e.target.value)} */
                        /* required */
                        readOnly
                    />
                </div>

                <div className="form-group">
                    <label>{dict.common.labels.phone}</label>
                    <div className="input-wrapper">
                        <input
                            type="tel"
                            value={phone ?? ""}
                            onChange={(e) => updateUser({phone: validatePhone(e.target.value)})}
                            maxLength={maxPhoneLen}
                        />
                        <span className="char-counter">{phone?.length} / {maxPhoneLen}</span>
                    </div>
                </div>
            </SettingSection>
        
            {/* Аватар */}
            <SettingSection title={dict.common.settings.labels.avatar}>
                <div className={styles.avatarOptions}>
                    <div
                        className={`${styles.avatarOption} ${avatarType === "uploaded" ? styles.selected : ""}`}
                        onClick={() => {
                            updateUser({ avatarType: "uploaded" });
                        }}
                    >
                        <img 
                            className={`${styles.avatarPreview} avatar`}
                            src={uploadedAvatar || "/images/upload-foto.png"} 
                            onError={(e) => {
                                e.currentTarget.src = "/images/upload-foto.png"; // путь к запасной картинке
                            }}
                            alt="Моя" />
                        <span>{dict.common.settings.avatarTypes.uploaded}</span>
                        <input
                            className='d-none'
                            type="file"
                            id="uploadImage"
                            accept="image/*"
                            onChange={handleImageUpload}
                        />
                        <label htmlFor="uploadImage" className={styles.uploadAvatar}>
                            <div><i className="fa-solid fa-upload"></i></div>
                        </label>
                    </div>
                    <div
                        className={`${styles.avatarOption} ${avatarType === "google" ? styles.selected : ""}`}
                        onClick={() => {
                            updateUser({ avatarType: "google" });
                        }}
                    >
                        <img 
                            className={`${styles.avatarPreview} avatar`}
                            src={user.googleAvatar || ""} 
                            alt="Google" />
                        <span>{dict.common.settings.avatarTypes.google}</span>
                    </div>
                    <div
                        className={`${styles.avatarOption} ${avatarType === "default" ? styles.selected : ""}`}
                        onClick={() => {
                            updateUser({ avatarType: "default" });
                        }}
                    >
                        <img 
                            className={`${styles.avatarPreview} avatar`} 
                            src="/images/placeholders/avatar-placeholder.png" 
                            alt="Default"
                        />
                        <span>{dict.common.settings.avatarTypes.default}</span>
                    </div>
                </div>
            </SettingSection>

            {/* Bio */}
            <SettingSection title={dict.common.settings.labels.bio}>
                <div className="form-group">
                    <p>{dict.tooltips.settings.bio}</p>
                    <div className="input-wrapper">
                        <textarea 
                            className={styles.bio}
                            value={bio ?? ""}
                            onChange={(e) => updateUser({ bio: e.target.value})}
                            maxLength={maxBioLen}
                            rows={4}
                        />
                        <span className="char-counter">{bio?.length} / {maxBioLen}</span>
                    </div>
                </div>
            </SettingSection>
        </>
    );
};

export default ProfileSettings;