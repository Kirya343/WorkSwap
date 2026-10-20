import { IPopupNotification } from "@core/lib/notification/types";
import styles from "./PopupNotification.module.scss"

interface PopupNotificationProps {
    onClose: () => void;
    notification: IPopupNotification
}

const PopupNotification = ({ onClose, notification }: PopupNotificationProps) => {
    if (!notification) return null;

    return (
        <div className={`${styles.notification} ${styles[notification.type]}`}>
            {notification.message}
            <button className={styles.close} onClick={onClose}><i className="fa-solid fa-xmark"></i></button>
        </div>
    );
};

export default PopupNotification;