"use client"

import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useCatalogFilters } from "@core/lib/common/contexts/CatalogFiltersContext";
import { useEffect, useState, useRef } from "react";
import { IShortListing } from '@core/lib/listing/types'
import { listingService } from '@core/lib/listing/services'
import PublicListingCard from "@core/components/ui/listings/cards/PublicListingCard/PublicListingCard";
import NewPublicListingCard from "@core/components/ui/listings/cards/PublicListingCard/NewPublicListingCard"
import ListingsGrid from "@/components/ui/listings/ListingsGrid/ListingsGrid";

const CatalogContent = ({initialListings}: {initialListings: IShortListing[]}) => {

    const { filters, setTotalPages } = useCatalogFilters();

    const [listings, setListings] = useState<IShortListing[]>(initialListings);
    const [loading, setLoading] = useState<boolean>(true);

    const lastRequestId = useRef<number>(0);

    useEffect(() => {
        const requestId = ++lastRequestId.current;

        async function loadCatalog() {
            try {
                const data = await listingService.getCatalog(filters)

                console.log(data)
                if (requestId === lastRequestId.current) {
                    setListings(data.content);
                    setTotalPages(data.page.totalPages);
                }
            } finally {
                setLoading(false)
            }
        }

        loadCatalog()
    }, [filters, setTotalPages]);

    return (
        <ListingsGrid>
            {!loading && listings?.map((listing) => (
                <PublicListingCard
                    key={listing.id}
                    listing={listing}
                />
            ))}
            <NewPublicListingCard />
        </ListingsGrid>
    );
};

export default CatalogContent;