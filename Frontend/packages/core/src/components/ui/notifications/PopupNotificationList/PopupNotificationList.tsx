import { createRef, useRef } from "react";
import { TransitionGroup, CSSTransition } from "react-transition-group";
import PopupNotification from "../PopupNotification/PopupNotification";
import { useNotification } from "@core/lib/notification/NotificationContext";
import styles from "./PopupNotificationList.module.scss"

const PopupNotificationList = () => {

    const nodeRefs = useRef<Record<number, React.RefObject<HTMLDivElement | null>>>({});
    const { deletePopupNotification, popupNotifications } = useNotification();

    return (
        <div className={styles.conteiner}>
            <TransitionGroup component={null}>
                {popupNotifications?.map((n) => {
                    if (!nodeRefs.current[n.id]) {
                        nodeRefs.current[n.id] = createRef<HTMLDivElement>();
                    }

                    return (
                        <CSSTransition
                            key={n.id}
                            nodeRef={nodeRefs.current[n.id]}
                            timeout={300}
                            classNames={{
                                enter: styles.enter,
                                enterActive: styles.enterActive,
                                exit: styles.exit,
                                exitActive: styles.exitActive,
                            }}
                        >
                            <div ref={nodeRefs.current[n.id]}>
                                <PopupNotification
                                    onClose={() => deletePopupNotification(n.id)}
                                    notification={n}
                                />
                            </div>
                        </CSSTransition>
                    );
                })}
            </TransitionGroup>
        </div>
    )
}

export default PopupNotificationList;