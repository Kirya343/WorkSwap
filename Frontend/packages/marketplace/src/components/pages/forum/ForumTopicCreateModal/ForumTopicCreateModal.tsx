import ForumTagSelector from "@/components/ui/selectors/ForumTagSelector";
import Modal from "@core/components/ui/Modal/Modal";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { forumService } from "@core/lib/forum/forumService";
import { ForumTag } from "@core/lib/forum/types";
import { useState } from "react";
import PaperPlaneIcon from "@core/components/common/icons/PaperPlaneIcon"
import styles from "./ForumTopicCreateModal.module.scss"
import { useRouter } from "next/navigation";
import Textarea from "@core/components/ui/primitives/Textarea/Textarea";

const ForumTopicCreateModal = ({tags}: {tags: ForumTag[] | null}) => {
    const { dict } = useI18n()
    
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [tag, setTag] = useState<ForumTag | null>(null);
    const [sending, setSending] = useState(false);
    const [isOpen, setOpen] = useState<boolean>(false)
    const router = useRouter();

    const createTopic = async () => {
        setSending(true);
        const newTopic = {
            createdAt: "",
            openId: "",
            title,
            content,
            tagName: tag?.name ?? "",
            language: "",
            author: { id: 0, sub: "", name: ""},
            posts: [],
            postsCount: 0
        }
        const topicOpenId: string = await forumService.createTopic(newTopic);
        setSending(false);
        if (topicOpenId) {
            setTitle('');
            router.push(`/forum/topic/${topicOpenId}`);
        }
    } 
    
    return (
        <>
            <button 
                className={`${styles.createBtn} btn btn-primary`} 
                onClick={() => setOpen(true)}
            >
                <i className="fa-solid fa-pen"/>{dict.buttons.forum.createTopic}
            </button>
            <Modal isOpen={isOpen} onClose={() => setOpen(false)} title="Создать тему">
                <div className={styles.form}>
                    <input 
                        value={title} 
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder='Введите название топика...'
                    />
                    <Textarea
                        value={content} 
                        onChange={(e) => setContent(e.target.value)} 
                        className={styles.contentInput}
                        placeholder='Контент топика' 
                        autoGrow
                    />
                    <button onClick={createTopic} className={`${styles.sendBtn} hover`} disabled={sending}>
                        <PaperPlaneIcon />
                    </button>
                </div>
                {tags && (
                    <div className={styles.tags}>
                        <ForumTagSelector tags={tags} currentTag={tag} onChange={setTag}/>
                    </div>
                )}
            </Modal>
        </>
    );
}

export default ForumTopicCreateModal;