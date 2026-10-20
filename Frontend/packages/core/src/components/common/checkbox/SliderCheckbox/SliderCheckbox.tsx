import { ChangeEvent, ReactNode } from "react";
import styles from "./SliderCheckbox.module.scss"
import clsx from "clsx";

interface SliderCheckboxProps {
    checked: boolean;
    onChange: (e: ChangeEvent<HTMLInputElement, HTMLInputElement>) => void;
    id: string;
    options?:
        | ReactNode
        | {
            checked: ReactNode;
            unchecked: ReactNode;
        };
    className?: string;
    icons?: ReactNode[]
}

const SliderCheckbox = ({
    checked,
    onChange,
    id,
    options,
    className,
    icons
}: SliderCheckboxProps) => {

    const isOptionsObject =
        typeof options === "object" &&
        options !== null &&
        "checked" in options &&
        "unchecked" in options;

    return (
        <div className={styles.toggle}>
            <label className={clsx(styles.switch, className)}>
                <input 
                    id={`${id}-checkbox`}
                    type="checkbox" 
                    checked={checked}
                    onChange={(e) => onChange(e)}
                    value="true"
                />
                <span className={styles.slider}>
                    {icons}
                </span>
            </label>
            <p>
                {isOptionsObject
                    ? checked
                        ? options.checked
                        : options.unchecked
                    : options}
            </p>
        </div>
    )
}

export default SliderCheckbox