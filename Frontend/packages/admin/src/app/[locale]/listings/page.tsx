"use client"

import { useCallback, useEffect, useState } from "react";
import Card from "@/components/ui/Card/Card";
import { Page } from "@core/lib/common/types/page";
import Loader from "@core/components/common/Loader/Loader";
import Pagination from "@core/components/ui/Pagination/Pagination";
import { listingService } from "@core/lib/listing/services";
import { IFullListing } from "@core/lib/listing/types";
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";
import ListingsTable from "@/components/pages/listings/ListingsTable/ListingsTable";

export default function ListingsPage() {

    const [sortParam, setSortParam] = useState<string>("id");
    const [listings, setListings] = useState<Page<IFullListing> | null>(null);

    const loadListings = useCallback(async (page: number) => {
        const data: Page<IFullListing> = await listingService.getListingsPage(page, 10, sortParam);
        setListings(data);
    }, [])
    
    useEffect(() => {
        loadListings(0);
    }, [sortParam])

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Управление объявлениями" },
                ]}
            />

            <Card>
                <Loader loadingActive={!listings?.content}>
                    {listings?.content && <ListingsTable listings={listings?.content}/>}
                    <Pagination
                        page={listings?.page.number || 0} 
                        totalPages={listings?.page.totalPages || 1} 
                        onChange={(page) => loadListings(page)}
                    />
                </Loader>
            </Card>
        </>
    )
}