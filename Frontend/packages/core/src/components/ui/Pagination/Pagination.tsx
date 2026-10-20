"use client"

import styles from "./Pagination.module.scss"
import clsx from "clsx"

interface PaginationProps {
    page: number;
    totalPages: number; 
    onChange: (page: number) => void;
}

// 0-based paginatation
const Pagination = ({page, totalPages, onChange}: PaginationProps) => {

    const getPageNumbers = () => {
        if (!totalPages) return []

        const maxButtons = 5;

        if (totalPages <= maxButtons) {
            return Array.from({ length: totalPages }, (_, i) => i);
        }

        const pages = [];

        // первая
        pages.push(0);

        let start = Math.max(page - 1, 1);
        let end = Math.min(page + 1, totalPages - 2);

        // если рядом с началом
        if (page <= 2) {
            start = 1;
            end = 3;
        }

        // если рядом с концом
        if (page >= totalPages - 3) {
            start = totalPages - 4;
            end = totalPages - 2;
        }

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }

        // последняя
        pages.push(totalPages - 1);

        return pages;
    }

    if (totalPages <= 0) {
        return null;
    }

    const isPrevDisabled = page <= 0;
    const isNextDisabled = page >= totalPages - 1;

    return (
        <div className={styles.pagination}>
            <button disabled={isPrevDisabled} onClick={() => onChange(page - 1)}>
                Назад
            </button>

            {getPageNumbers().map(p => (
                <button
                    key={p}
                    className={clsx(styles.pageNumber, p === page ? styles.active : "")}
                    onClick={() => onChange(p)}
                >
                    {p + 1}
                </button>
            ))}

            <button 
                disabled={isNextDisabled} 
                onClick={() => onChange(page + 1)}
            >
                Вперёд
            </button>
        </div>
    )
}

export default Pagination;