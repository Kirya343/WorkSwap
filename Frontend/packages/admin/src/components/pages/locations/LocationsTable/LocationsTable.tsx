import PenIcon from "@core/components/common/icons/PenIcon"
import TrashIcon from "@core/components/common/icons/TrashIcon"
import styles from "./LocationsTable.module.scss"
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
import { ILocation } from '@core/lib/location/types';
import PlusIcon from "@core/components/common/icons/PlusIcon";
import AdminTable from "@/components/ui/AdminTable/AdminTable";

interface LocationTableProps {
    locations: ILocation[];
    onEditLocation: (id: number) => void;
    onDeleteLocation: (id: number) => void;
    onCreateLocation: (id: number) => void;
}

type LocationItem = {
    id: number;
    title: string;
    countryId: number | null;
    subRows?: LocationItem[];
};

const LocationsTable = ({
    locations, 
    onEditLocation,
    onDeleteLocation,
    onCreateLocation
}: LocationTableProps) => {

    const { dict } = useI18n();

    const [expanded, setExpanded] = useState<ExpandedState>({});

    const buildTree = (
        countryId: number | null
    ): LocationItem[] => {
        return locations
            .filter(loc => loc.countryId === countryId)
            .map(loc => {
                const subRows = buildTree(loc.id);

                return {
                    id: loc.id,
                    title: loc.name,
                    countryId: loc.countryId,
                    ...(subRows.length > 0 ? { subRows } : {}),
                };
            });
    };

    const data = useMemo(
        () => buildTree(null),
        [locations, dict]
    );

    const features = tableFeatures({
        rowExpandingFeature,
        expandedRowModel: createExpandedRowModel(),
    })

    const columns = useMemo<ColumnDef<typeof features, LocationItem>[]>(
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
                            style={{
                                paddingLeft: `${row.depth * 1.5}rem`,
                            }}
                        >
                            {getValue<string>()}
                        </span>

                        <button 
                            className={styles.createLocation} 
                            onClick={() => onCreateLocation(row.getValue<number>("id"))}
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
                            className={clsx("btn", "btn-primary", styles.action)}
                            onClick={() => onEditLocation(row.original.id)}
                        >
                            <PenIcon className={styles.icon} />
                        </button>

                        <button
                            className={clsx("btn", "btn-danger", styles.action)}
                            onClick={() => onDeleteLocation(row.original.id)}
                        >
                            <TrashIcon className={styles.trashIcon} />
                        </button>
                    </div>
                ),
            }
        ], [onEditLocation, onDeleteLocation]
    );

    const table = useTable({
        key: 'locations-table', // needed for devtools, omit if you don't want to use the devtools
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

export default LocationsTable;