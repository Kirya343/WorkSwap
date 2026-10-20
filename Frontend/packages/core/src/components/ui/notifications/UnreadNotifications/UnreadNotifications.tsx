import styles from "./UnreadNotifications.module.scss";

const UnreadNotifications = ({count}: {count: number}) => {
    return (
        <span className={styles.unreadCount}>
            {count}
        </span>
    )
}

export default UnreadNotifications;