"use client"

import { useEffect, useState } from "react";
import NotFoundPage from "@core/pages/NotFoundPage";
import { useParams } from "next/navigation";
import { IListingPageRequest } from "@core/lib/listing/types";
import { IShortUserProfile } from "@core/lib/user/types";
import { listingService } from "@core/lib/listing/services";
import ListingPageLayout from "../../../../components/pages/listing/ListingPageLayout/ListingPageLayout";
import PriceTypes from "@core/components/common/PriceTypes/PriceTypes";
import RatingStars from "@core/components/common/RatingStars/RatingStars";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import ListingDetail from "@/components/pages/listing/ListingDetail/ListingDetail";

const ListingPage = () => {

    const { id } = useParams();
    const listigId = Number(id);
    const { dict } = useI18n();

    const [listingPage, setListing] = useState<IListingPageRequest | null>(null);
    const [author, setAuthor] = useState<IShortUserProfile | null>(null);
    const [error, setError] = useState<boolean>(false);

    useEffect(() => {
        listingService.getPageById(listigId)
            .then(listing => {
                setListing(listing)
                setAuthor(listing.author)
            })
            .catch(() => setError(true))
    }, [listigId]);

    if (error) return <NotFoundPage/>;

    return listingPage && (
        <ListingPageLayout
            listingPage={listingPage} 
            author={author}
            details={(
                <>
                    <ListingDetail title={dict.common.labels.price} customValue>
                        <h1><PriceTypes listing={listingPage.listing} className={"listing"} /></h1>
                    </ListingDetail>
                    
                    <ListingDetail title={dict.common.labels.location}>
                        {listingPage.listing?.location || ""}
                    </ListingDetail>
                    
                    <ListingDetail title={dict.common.labels.rating} customValue>
                        <RatingStars rating={listingPage.listing?.rating ?? 0}/>
                    </ListingDetail>
                </>
            )}   
        />
    );
};

export default ListingPage;