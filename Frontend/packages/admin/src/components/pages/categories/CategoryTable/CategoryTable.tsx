import { ICategory } from '@core/lib/category/types';
import PenIcon from "@core/components/common/icons/PenIcon"
import TrashIcon from "@core/components/common/icons/TrashIcon"
import styles from "./CategoryTable.module.scss"
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import {
    flexRender,
    tableFeatures,
    useTable,
    rowExpandingFeature,
    createExpandedRowModel,
} from "@tanstack/react-table";
import type { ColumnDef, ExpandedState } from '@tanstack/react-table'
import { useMemo, useState } from 'react';
import clsx from "clsx";
import PlusIcon from '@core/components/common/icons/PlusIcon';
import AdminTable from '@/components/ui/AdminTable/AdminTable';

interface CategoryTableProps {
    type: string;
    categories: ICategory[];
    onEditCategory: (id: number) => void;
    onDeleteCategory: (id: number) => void;
    onCreateCategory: (parentId: number) => void
}

type CategoryItem = {
    id: number;
    title: string;
    parentId: number | null;
    subRows?: CategoryItem[];
};

const CategoryTable = ({
    type,
    categories, 
    onEditCategory,
    onDeleteCategory,
    onCreateCategory
}: CategoryTableProps) => {

    const { dict } = useI18n();

    const [expanded, setExpanded] = useState<ExpandedState>({});

    const buildTree = (
        parentId: number | null
    ): CategoryItem[] => {
        return categories
            .filter(category => category.parentId === parentId)
            .map(category => {
                const subRows = buildTree(category.id);

                const translate = dict.categories.category[type][category.name];

                return {
                    id: category.id,
                    title: translate || `${category.name} (Категория не переведена)`,
                    parentId: category.parentId,
                    ...(subRows.length > 0 ? { subRows } : {}),
                };
            });
    };

    const data = useMemo(
        () => buildTree(null),
        [categories, type, dict]
    );

    const features = tableFeatures({
        rowExpandingFeature,
        expandedRowModel: createExpandedRowModel(),
    })

    const columns = useMemo<ColumnDef<typeof features, CategoryItem>[]>(
        () => [
            {
                id: "expander",
                header: "",
                wight: "",
                cell: ({ row }) => {
                    if (!row.getCanExpand()) {
                        return null;
                    }

                    return (
                        <button
                            type="button"
                            className={styles.expanderButton}
                            onClick={row.getToggleExpandedHandler()}
                        >
                            {row.getIsExpanded() ? "▼" : "▶"}
                        </button>
                    );
                },
            },
            {
                accessorKey: 'id',
                header: 'ID',
                cell: (info) => info.getValue(),
            },
            {
                accessorKey: 'title',
                header: 'Title',
                cell: ({ row, getValue }) => (
                    <>
                        <span
                            style={{ paddingLeft: `${row.depth * 1.5}rem` }}
                        >
                            {getValue<string>()}
                        </span>

                        <button 
                            className={styles.createCategory} 
                            onClick={() => onCreateCategory(row.getValue<number>("id"))}
                        >
                            <PlusIcon/>
                        </button>
                    </>
                ),
            },
            {
                id: "actions",
                header: 'Actions',
                cell: ({ row }) => (
                    <div className={styles.actions}>
                        <button
                            className={`btn btn-primary ${styles.action}`}
                            onClick={() => onEditCategory(row.original.id)}
                        >
                            <PenIcon className={styles.icon} />
                        </button>

                        <button
                            className={`btn btn-danger ${styles.action}`}
                            onClick={() => onDeleteCategory(row.original.id)}
                        >
                            <TrashIcon className={styles.trashIcon} />
                        </button>
                    </div>
                ),
            }
        ], [onEditCategory, onDeleteCategory]
    );

    const table = useTable({
        key: 'category-table', // needed for devtools, omit if you don't want to use the devtools
        features,
        columns,
        data,
        getRowId: row => String(row.id),
        getSubRows: row => row.subRows,


        state: {
            expanded,
        },

        onExpandedChange: setExpanded,
    });

    return <AdminTable table={table} extraStyles={styles}/>;
};

export default CategoryTable;