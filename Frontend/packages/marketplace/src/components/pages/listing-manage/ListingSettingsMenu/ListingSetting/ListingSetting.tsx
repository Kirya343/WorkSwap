import React, { ReactNode, useMemo, useState } from "react";
import styles from "./ListingSetting.module.scss"
import clsx from "clsx";

interface ListingSettingProps {
    title: string;
    children: ReactNode;
}
const ListingSetting = ({ title, children}: ListingSettingProps) => {

    const [active, setActive] = useState<boolean>(false);

    const validChildren = useMemo(
        () => React.Children.toArray(children).filter(Boolean),
        [children]
    );

    if (validChildren.length === 0) return null;

    return (
        <div className={styles.setting}>
            <div className={styles.header} onClick={() => setActive(!active)}>
                <h3>{title}</h3>
            </div>
            <div className={clsx(styles.body, active ? styles.active : "")}>
                <div>
                    {children}
                </div>
            </div>
        </div>
    );
}
export default ListingSetting;