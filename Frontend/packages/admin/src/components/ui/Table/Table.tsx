import { ReactNode } from "react";
import styles from "./Table.module.scss"

interface Column {
    title: string;
    sortable?: true;
}

export type Columns = Record<string, Column>;

export interface TableItem {
    actions: ReactNode[];

    [key: string]: string | number | boolean | ReactNode | ReactNode[];
}

interface TableProps {
    href?: string;
    columns: Columns;
    items: TableItem[];
    className?: string;
    onColumnClick?: (sort: string) => void
}

export default function Table({href, columns, items, className, onColumnClick}: TableProps) {

    const handleRowClick = () => {
        if (!href || onColumnClick) return
        window.location.href = href;
    };

    return (
        <div className={`${styles.wrapper} ${className}`}>
            <table className={`${styles.table}`}>
                <thead onClick={handleRowClick} style={href ? {cursor: "pointer"} : {}}>
                    <tr>
                        {Object.keys(columns).map(key => (
                            <th 
                                key={`column-${key}`}
                                className={`${styles.tableHeadItem} ${(columns[key].sortable && onColumnClick) ? styles.sortable : ""}`}
                                onClick={(columns[key].sortable && onColumnClick) ? () => onColumnClick(key) : undefined}
                            >{columns[key].title}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {items.length > 0 ? (
                        items.map((item, index) => (
                            <tr key={`table-item-${index}`}>
                                {Object.keys(columns)
                                    .map(columnKey => (
                                        <td key={columnKey} className={styles[columnKey]}>
                                            {item[columnKey] || "—"}
                                        </td>
                                    ))
                                }
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={Object.keys(columns).length}>Нет данных</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}