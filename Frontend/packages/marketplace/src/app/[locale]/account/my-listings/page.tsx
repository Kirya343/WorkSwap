"use client"

import { useEffect, useState } from "react";
import { IFullListing } from "@core/lib/listing/types";
import { listingService } from "@core/lib/listing/services";
import ListingDraftItem from "@core/components/ui/listings/cards/DraftListingCard/DraftListingCard";
import Tooltip from "@core/components/common/Tooltip/Tooltip"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import Loader from "@core/components/common/Loader/Loader"
import PlusIcon from "@core/components/common/icons/PlusIcon";
import draftStyles from "@core/components/ui/listings/cards/DraftListingCard/DraftListingCard.module.scss"
import privateStyles from "@core/components/ui/listings/cards/PrivateListingCard/PrivateListingCard.module.scss"
import AccountHeader from "@/components/pages/account/AccountHeader/AccountHeader";
import styles from "./MyListingsPage.module.scss"
import AccountListingsGrid from "@/components/pages/account/AccountListingsGrid/AccountListingsGrid";
import HarizontalListingCard from "@core/components/ui/listings/cards/HorizontalListingCard/HarizontalListingCard"
import { useRouter } from "next/navigation";

const MyListingsPage = () => {

    const { dict } = useI18n();

    const [listings, setListings] = useState<IFullListing[] | null>(null);
    const [activeListings, setActiveListings] = useState<IFullListing[] | null>(null);
    const [drafts, setDrafts] = useState<IFullListing[] | null>(null);
    const [loading, setLoading] = useState<boolean>(true)
    const router = useRouter();
    
    useEffect(() => {
        listingService.getMyListings().then((data: IFullListing[]) => {
            setLoading(false);
            setListings(data);
            setActiveListings(data.filter(listing => !listing.temporary));
            setDrafts(data.filter(listing => listing.temporary));
        })
    }, []);

    return (
        <>
            <AccountHeader>
                <h2>{dict.common.titles.myListings} {listings && `(${listings?.length})`}</h2>
                <button
                    className="btn btn-primary"
                    onClick={() => router.push("/account/listing/create")}
                >
                    {dict.buttons.listing.createListing}
                </button>
            </AccountHeader>

            <Loader loadingActive={loading}>
                {listings?.length == 0 ? (
                    <AccountListingsGrid>
                        <article 
                            onClick={() => router.push("/account/listing/create")} 
                            className={`${privateStyles.card} ${privateStyles.center} hover-animation-card`}
                        >
                            <h3>{dict.navigation.catalogSidebar.links.createListing}</h3>
                        </article>
                    </AccountListingsGrid>
                ) : (
                    <div className={styles.content}>
                        {listings?.slice()
                            .sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
                            .map((listing) => (
                                <HarizontalListingCard
                                    key={listing.id}
                                    listing={listing}
                                /> 
                            ))
                        }
                    </div>
                )}
            </Loader>
        </>
    );
};

export default MyListingsPage;