"use client"

import { ReactNode, useEffect, useRef } from 'react';
import styles from "./Modal.module.scss";
import clsx from "clsx";

interface ModalProps {
    isOpen: boolean;
    onClose: () => void,
    title: string;
    id?: string;
    className?: string;
    children: ReactNode;
}

const Modal = ({ isOpen, onClose, title, id = 'normalModal', children, className }: ModalProps) => {
    const dialogRef = useRef<HTMLDialogElement | null>(null);

    useEffect(() => {
        const dialog = dialogRef.current;
        if (!dialog) return;

        if (isOpen) {
            dialog.showModal();
        } else if (dialog.open) {
            dialog.close();
        }

        const handleCancel = (e: Event) => {
            e.preventDefault();
            onClose();
        };

        dialog.addEventListener("cancel", handleCancel);
        return () => dialog.removeEventListener("cancel", handleCancel);
    }, [isOpen, onClose]);

    return (
        <dialog ref={dialogRef} className={clsx(styles.modal, className, "fade-down")} onClick={(e) => e.stopPropagation()} id={id}>
            <button className={clsx(styles.close, "hover")} onClick={onClose}>✖</button>
            {title && <h2>{title}</h2>}
            {children}
        </dialog>
    );
};

export default Modal;