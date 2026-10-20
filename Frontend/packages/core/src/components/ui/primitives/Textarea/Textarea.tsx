import styles from "./Textarea.module.scss";

interface TextareaProps
    extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
    autoGrow?: boolean;
}

const Textarea = ({
    autoGrow = false,
    className,
    onChange,
    ...props
}: TextareaProps) => {
    const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        if (autoGrow) {
            e.currentTarget.style.height = "0px";
            e.currentTarget.style.height = `${e.currentTarget.scrollHeight}px`;
        }

        onChange?.(e);
    };

    return (
        <textarea
            {...props}
            className={`${styles.textarea} ${className ?? ""}`}
            onChange={handleChange}
        />
    );
};

export default Textarea