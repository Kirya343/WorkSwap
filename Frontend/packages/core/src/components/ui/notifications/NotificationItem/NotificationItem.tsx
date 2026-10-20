import { INotification } from "@core/lib/notification/types"
import styles from "./NotificationItem.module.scss"

interface NotificationItemProps {
    notification: INotification;
    onRead: (n: INotification) => void;
}

const NotificationItem = ({ notification, onRead }: NotificationItemProps) => {

    return (
        <div
            className={`${styles.notification} ${notification.importance?.toLowerCase() || styles.info} ${!notification.isRead ? styles.unread : ""}`}
            onClick={() => onRead(notification)}
        >
            <div className={styles.body}>
                <strong className={styles.title}>{notification.title}</strong>
                <span className={styles.content}>{notification.content}</span>
            </div>
        </div>
    );
};

export default NotificationItem;