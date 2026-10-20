import PriceTypes from "@core/components/common/PriceTypes/PriceTypes";
import { IShortListing } from "@core/lib/listing/types";
import { ColumnDef, createExpandedRowModel, tableFeatures, rowExpandingFeature, useTable } from "@tanstack/react-table";
import Link from "next/link";
import { useMemo } from "react";
import styles from "./ListingsTable.module.scss"
import clsx from "clsx"
import EyeIcon from "@core/components/common/icons/EyeIcon";
import AdminTable from "@/components/ui/AdminTable/AdminTable";
import FormattedDate from "@core/components/common/date/FormattedDate";

type ListingItem = {
    id: number;
    title: string;
    price: number;
    date: string;
};

interface ListingsTableProps {
    listings: IShortListing[]
}

const ListingsTable = ({listings}: ListingsTableProps) => {

    const data: ListingItem[] = useMemo(
        () => listings.map(l => ({
            id: l.id,
            title: l.localizedTitle,
            price: l.price,
            date: l.publishedAt
        })),
        [listings]
    );

    const features = tableFeatures({
        rowExpandingFeature,
        expandedRowModel: createExpandedRowModel(),
    })

    const columns = useMemo<ColumnDef<typeof features, ListingItem>[]>(
        () => [
            {
                accessorKey: 'id',
                header: 'ID',
                cell: (info) => info.getValue(),
            },
            {
                accessorKey: 'title',
                header: 'Title',
                cell: (info) => info.getValue(),
            },
            {
                accessorKey: 'price',
                header: 'Price',
                cell: ({ row }) => {

                    const listing = listings.find(l => l.id == row.getValue<number>("id"))

                    return listing ? <PriceTypes listing={listing} /> : "-"
                },
            },
            {
                accessorKey: 'date',
                header: 'Date',
                cell: (info) => <FormattedDate isoDate={String(info.getValue())} format="DMY"/>,
            },
            {
                id: "actions",
                header: 'Actions',
                cell: ({ row }) => (
                    <div className={styles.actions}>
                        <Link
                            key={`action-viewListing-${row.getValue<number>("id")}`} 
                            href={`/listing/${row.getValue<number>("id")}`} 
                            className={clsx("btn", "btn-primary", styles.action)}
                        >
                            <EyeIcon className={styles.icon} />
                        </Link>
                    </div>
                ),
            }
        ], [listings]
    );

    const table = useTable({
        key: 'listings-table', // needed for devtools, omit if you don't want to use the devtools
        features,
        columns,
        data,
        getRowId: row => String(row.id),
    });

    return <AdminTable table={table} extraStyles={styles} />
}

export default ListingsTable;