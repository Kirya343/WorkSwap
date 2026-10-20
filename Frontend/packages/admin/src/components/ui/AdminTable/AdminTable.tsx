import { flexRender, RowData, Table, TableFeatures, type Table as ReactTable } from "@tanstack/react-table";
import styles from "./AdminTable.module.scss"
import clsx from "clsx"

interface AdminTableProps<
    TFeatures extends TableFeatures,
    TData extends RowData
> {
    table: Table<TFeatures, TData>;
    extraStyles: { readonly [key: string]: string };
}

const AdminTable = <
    TFeatures extends TableFeatures,
    TData extends RowData
>({
    table,
    extraStyles
}: AdminTableProps<TFeatures, TData>) => {
    return (
        <div className={styles.tableWrapper}>
            <table className={styles.table}>

                <colgroup>
                    {table.getHeaderGroups()[0]?.headers.map(header => (
                        <col
                            key={header.id}
                            className={extraStyles[`${header.column.id}-column`]}
                        />
                    ))}
                </colgroup>

                <thead className={styles.thead}>
                    {table.getHeaderGroups().map(headerGroup => (
                        <tr className={clsx(styles.tr, extraStyles.tr)} key={headerGroup.id}>
                            {headerGroup.headers.map(header => (
                                <th
                                    className={styles.th}
                                    key={header.id}
                                >
                                    {flexRender(
                                        header.column.columnDef.header,
                                        header.getContext()
                                    )}
                                </th>
                            ))}
                        </tr>
                    ))}
                </thead>

                <tbody className={styles.tbody}>
                    {table.getRowModel().rows.map(row => (
                        <tr className={clsx(styles.tr, extraStyles.tr)} key={row.id}>
                            {row.getAllCells().map(cell => (
                                <td
                                    className={clsx(styles.td, extraStyles.td, extraStyles[`${cell.column.id}-column`])}
                                    key={cell.id}
                                >
                                    {flexRender(
                                        cell.column.columnDef.cell,
                                        cell.getContext()
                                    )}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

export default AdminTable;