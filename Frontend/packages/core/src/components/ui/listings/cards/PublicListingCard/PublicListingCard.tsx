"use client"

import { IShortListing } from "@core/lib/listing/types";
import { useAuth } from "@core/lib/auth/AuthContext";
import { listingService } from "@core/lib/listing/services";
import PriceTypes from "@core/components/common/PriceTypes/PriceTypes"
import { listingTypesWithRating } from "@core/lib/listing/constants/listingTypes";
import RatingStars from "@core/components/common/RatingStars/RatingStars"
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import HeartIcon from "@core/components/common/icons/HeartIcon"
import styles from "./PublicListingCard.module.scss"
import LocationIcon from "@core/components/common/icons/LocationIcon"
import { useRouter } from "next/navigation";

const PublicListingCard = ({listing}: {listing: IShortListing}) => {

    const { dict } = useI18n()
    const { isAuthenticated } = useAuth();
    const router = useRouter();

    const isNew = (new Date().getTime() - new Date(listing.publishedAt).getTime()) < 3 * 24 * 60 * 60 * 1000;
    const { isFavorite, toggleFavorite, likesCount } = listingService.useFavorite(listing);

    // console.log(listing.id, "Объявление: ", listing.localizedTitle)

    const navigator = () => {
        if (listing.type == "EVENT") {
            router.push(`/listing/${listing.id}/event`)
        } else {
            router.push(`/listing/${listing.id}`)
        }
    }

    return (
        <article onClick={() => navigator()} className={`${styles.card} fade-down`}>

            <div 
                className={styles.imageWrapper}
                style={{ 
                    "--bg-image": `url(${listing.imagePath || "/images/placeholders/default-listing.svg"})` 
                } as React.CSSProperties}
            >
                <img
                    src={listing.imagePath || "/images/placeholders/default-listing.svg"}
                    alt="Изображение объявления"
                    onError={(e) => {
                        const img = e.currentTarget;

                        if (img.dataset.fallback === "true") {
                            img.style.display = "none";
                            return;
                        }

                        img.dataset.fallback = "true";
                        img.src = "/images/placeholders/default-listing.svg";
                    }}
                />
                {isAuthenticated && (
                    <div className={styles.actions}>
                        <span id="likesCount">{likesCount}</span>
                        <HeartIcon 
                            filled={isFavorite}
                            size={24}
                            className={"like"}
                            onClick={toggleFavorite}
                        />
                    </div>
                )}
            </div>

            {isNew && (
                <div className={`${styles.listingStatus} red`}>{dict.tooltips.catalog.newListing}</div>
            )}
            
            <div className={styles.body}>
                <h3 className={styles.title}>{listing.localizedTitle}</h3>
                {/* <p className="text">{listing.localizedDescription}</p> */}

                <PriceTypes className={styles.price} listing={listing} />
                {listingTypesWithRating.includes(listing.type) && listing.rating > 0 && (
                    <RatingStars rating={listing.rating} />
                )}
                <span className={styles.location}>
                    <LocationIcon /> {listing.location}
                </span>
            </div>
        </article>
    );
};

export default PublicListingCard;