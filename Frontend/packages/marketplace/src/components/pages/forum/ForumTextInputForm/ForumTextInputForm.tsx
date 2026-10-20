import Avatar from "@core/components/common/Avatar/Avatar";
import styles from "./ForumTextInputForm.module.scss"
import Textarea from "@core/components/ui/primitives/Textarea/Textarea";
import { useState } from "react";
import { useAuth } from "@core/lib/auth/AuthContext";
import PaperPlaneIcon from "@core/components/common/icons/PaperPlaneIcon";
import clsx from "clsx";

interface ForumTextInputForm {
    onFormSend: (content: string) => Promise<void>;
    disabled?: boolean;
    placeholder?: string;
    addAvatar?: boolean;
    className?: string;
}

const ForumTextInputForm = ({ onFormSend, disabled, placeholder, addAvatar, className }: ForumTextInputForm) => {

    const { user } = useAuth();
    const [content, setContent] = useState<string>("");

    const handleSend = async () => {
        await onFormSend(content);
        setContent("");
    };

    return (
        <div className={clsx(styles.form, className)}>
            {addAvatar && <Avatar user={user} size={40} />}

            <Textarea
                value={content} 
                onChange={(e) => setContent(e.target.value)} 
                placeholder={placeholder || 'Напишите ответ...'} 
                autoGrow
            />

            {content.length > 0 && (
                <button 
                    onClick={handleSend} 
                    disabled={disabled}
                    className={clsx(styles.sendBtn, "hover")}
                >
                    <PaperPlaneIcon />
                </button>
            )}
        </div>
    );
}

export default ForumTextInputForm;