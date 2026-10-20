"use client"

import PublicListingCard from "@core/components/ui/listings/cards/PublicListingCard/PublicListingCard";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { listingService } from "@core/lib/listing/services";
import AccountHeader from "@/components/pages/account/AccountHeader/AccountHeader";
import Loader from "@core/components/common/Loader/Loader";
import AccountListingsGrid from "@/components/pages/account/AccountListingsGrid/AccountListingsGrid";

const FavoritesPage = () => {

    const { dict } = useI18n()

    const {loading, listings} = listingService.useMyFavorites();

    return (
        <>
            <AccountHeader title={dict.common.titles.favorites} />

            <Loader loadingActive={loading}>
                <AccountListingsGrid>
                    {listings?.slice()
                        .sort((a, b) => new Date(b.publishedAt).getTime() - new Date(a.publishedAt).getTime())
                        .map((listing) => (
                            <PublicListingCard 
                                key={listing.id}
                                listing={listing}
                            />
                        ))
                    }
                </AccountListingsGrid>
            </Loader>
        </>
    );
};

export default FavoritesPage;