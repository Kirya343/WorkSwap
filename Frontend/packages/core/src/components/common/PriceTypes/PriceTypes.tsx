"use client";

import { useI18n } from '@core/lib/common/contexts/I18nContext';
import styles from "./PriceTypes.module.scss"
import { IShortListing } from '@core/lib/listing/types';

interface Props {
    listing: IShortListing;
    className?: string;
}

const PriceTypes = ({ listing, className = "" }: Props) => {

    const { dict } = useI18n()

    if (!listing) return <span className={`price ${className}`} >Цена не указана</span>;

    const priceTypePlaceholder = dict.common.priceTypes[listing.priceType ? listing.priceType : 'FIXED']

    switch (listing.priceType) {
        case "NEGOTIABLE":
            return <span className={`price ${className}`}>{priceTypePlaceholder}</span>;

        case "SWAP":
            return <span className={`price ${className}`}>{priceTypePlaceholder}</span>;

        case "FREE":
            return <span className={`price ${className}`}>{priceTypePlaceholder}</span>;

        case "WANTED_FREE":
            return <span className={`price ${className}`}>{priceTypePlaceholder}</span>;

        default:
            return (
                <span className={`${styles.price} ${className}`}>
                    {listing.price || 0} {priceTypePlaceholder}
                </span>
            );
    }
};

export default PriceTypes;