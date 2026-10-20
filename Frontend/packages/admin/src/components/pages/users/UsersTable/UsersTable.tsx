import { ColumnDef, createExpandedRowModel, tableFeatures, rowExpandingFeature, useTable } from "@tanstack/react-table";
import Link from "next/link";
import { useMemo } from "react";
import styles from "./UsersTable.module.scss"
import clsx from "clsx"
import AdminTable from "@/components/ui/AdminTable/AdminTable";
import FormattedDate from "@core/components/common/date/FormattedDate";
import { IUser } from "@core/lib/user/types";
import UserGearIcon from "@core/components/common/icons/UserGearIcon";

type UserItem = {
    sub: string;
    name: string;
    email: string;
    date: string;
};

interface UsersTableProps {
    users: IUser[]
}

const UsersTable = ({users}: UsersTableProps) => {

    const data: UserItem[] = useMemo(
        () => users.map(u => ({
            sub: u.sub,
            name: u.name,
            email: u.email,
            date: u.createdAt
        })),
        [users]
    );

    const features = tableFeatures({
        rowExpandingFeature,
        expandedRowModel: createExpandedRowModel(),
    })

    const columns = useMemo<ColumnDef<typeof features, UserItem>[]>(
        () => [
            {
                accessorKey: 'sub',
                header: 'ID',
                cell: (info) => info.getValue(),
            },
            {
                accessorKey: 'name',
                header: 'Name',
                cell: (info) => info.getValue(),
            },
            {
                accessorKey: 'email',
                header: 'Email',
                cell: (info) => info.getValue(),
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
                            key={`action-viewUser-${row.getValue<number>("id")}`} 
                            href={`/users/${row.original.sub}`} 
                            className={clsx("btn", "btn-primary", styles.action)}
                        >
                            <UserGearIcon className={styles.icon} />
                        </Link>
                    </div>
                ),
            }
        ], [users]
    );

    const table = useTable({
        key: 'users-table', // needed for devtools, omit if you don't want to use the devtools
        features,
        columns,
        data,
        getRowId: row => String(row.sub),
    });

    return <AdminTable table={table} extraStyles={styles} />
}

export default UsersTable;