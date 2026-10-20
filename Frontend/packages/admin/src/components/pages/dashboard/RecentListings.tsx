"use client"

import { useEffect, useState } from "react";
import { listingService } from "@core/lib/listing/services"
import { IShortListing } from "@core/lib/listing/types";
import ListingsTable from "../listings/ListingsTable/ListingsTable";

const RecentListings = () => {

    const [listings, setListings] = useState<IShortListing[] | null>(null);

    useEffect(() => {
        const loadListings = async () => {
            try {
                const data = await listingService.getRecentListings(3);
                setListings(data);
            } catch (err) {
                console.error(err);
            }
        };

        loadListings();
    }, []);

    return listings && (
        <>
            <h2>Последние объявления</h2>
            {listings && <ListingsTable listings={listings}/>}
        </>
    )
};

export default RecentListings;