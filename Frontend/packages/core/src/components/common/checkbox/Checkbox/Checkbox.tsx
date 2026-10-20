import clsx from "clsx"
import { ChangeEvent, ReactNode } from "react";
import styles from "./Checkbox.module.scss";

interface Checkbox {
    checked: boolean;
    onChange: (e: ChangeEvent<HTMLInputElement, HTMLInputElement>) => void;
    id: string;
    children: ReactNode;
    className?: string;
}

const Checkbox = ({
    checked,
    onChange,
    id,
    children,
    className
}: Checkbox) => {
    return (
        <div 
            className={clsx(styles.checkbox, className)}
            id={id}
        >
            <input
                type="checkbox"
                id="translationsCheckbox"
                name="translationsCheckbox"
                checked={checked}
                onChange={(e) => onChange(e)}
            />
            <label htmlFor="translationsCheckbox">
                <span className={styles.checkmark}></span>
                <span>{children}</span>
            </label>
        </div>
    )
}

export default Checkbox;