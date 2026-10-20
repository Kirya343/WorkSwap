"use client";

import StarIcon from "../icons/StarIcon";
import styles from "./RatingStars.module.scss"

function RatingStars({ rating }: { rating: number }) {
    const countedRating = Math.max(0, Math.min(5, Number(rating ?? 0)));

    const stars = Array.from({ length: 5 }, (_, idx) => {
        const i = idx + 1;

        if (countedRating >= i) {
            return <StarIcon key={i} filled className={`${styles.star} ${styles.selected}`} />;
        }

        if (countedRating >= i - 0.5) {
            return <StarIcon key={i} half className={`${styles.star} ${styles.selected}`} />;
        }

        return <StarIcon key={i} className={`${styles.star} ${styles.selected}`} />;
    });

    return <div className={`${styles.rating} normal-only`}>{stars}</div>;
}

export default RatingStars;