"use client";

import { redirect } from 'next/navigation';
import { IFullListing } from '@core/lib/listing/types';
import { useI18n } from '@core/lib/common/contexts/I18nContext';
import styles from "./DraftListingCard.module.scss"

const DraftListingCard = ({ listing }: {listing: IFullListing}) => {

    const { dict } = useI18n()

    return (
        <article className={styles.card} onClick={() => redirect(`/account/listing/edit/${listing.id}`)}>
            <img 
                src={listing.imagePath || `/images/placeholders/default-listing.svg`}
                onError={(e) => { e.currentTarget.src = `/images/placeholders/default-listing.svg`; }}
            />
            <div className={styles.body}>
                {listing.localizedTitle ? (
                    <span>{listing.localizedTitle}</span>
                ) : (
                    <span>{dict.common.fallbacks.noTitle}</span>
                )}
            </div>
        </article>
    );
};

export default DraftListingCard;