"use client"

import { IReview } from "@core/lib/review/types";
import ReviewItem from "@core/components/ui/reviews/ReviewItem/ReviewItem";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import styles from "./ReviewsList.module.scss"

const ReviewsList = ({ reviews }: {reviews: IReview[] | null}) => {

    const { dict } = useI18n();

    return (
        <section className={styles.reviews}>
            <h2>{dict.common.reviews.listLabel}</h2>
            <div className={styles.list}>
                {reviews?.map((review) => (
                    <ReviewItem review={review} key={review.id}/>
                ))}
            </div>
        </section>
    );
};

export default ReviewsList;