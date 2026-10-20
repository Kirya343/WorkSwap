"use client"

import AccountHeader from "@/components/pages/account/AccountHeader/AccountHeader";
import Loader from "@core/components/common/Loader/Loader";
import ReviewItem from "@core/components/ui/reviews/ReviewItem/ReviewItem";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { reviewsService } from "@core/lib/review/reviewsService";
import { IReview } from "@core/lib/review/types";
import { useEffect, useState } from "react";
import styles from "./ReviewsPage.module.scss"

interface MyReviews {
    given: IReview[];
    recived: IReview[]
}

const ReviewsPage = () => {

    const [reviews, setReviews] = useState<MyReviews>({given: [], recived: []})
    const [loading, setLoading] = useState(true);
    const { dict } = useI18n()

    useEffect(() => {
        async function load() {
            try {
                const data = await reviewsService.getMyReviews()
                setReviews(data);
            } finally {
                setLoading(false)
            }
        }

        load()
    }, [])

    return (
        <>
            <AccountHeader title={dict.common.titles.myReviews}/>

            <Loader loadingActive={loading}>
                <div className={styles.content}>
                    <h3>Полученные ({reviews.recived.length})</h3>
                    {reviews.recived?.slice()
                        .map((review) => (
                            <ReviewItem
                                key={review.id}
                                review={review}
                            /> 
                        ))
                    }
                    <h3>Оставленные({reviews.given.length})</h3>
                    {reviews.given?.slice()
                        .map((review) => (
                            <ReviewItem
                                key={review.id}
                                review={review}
                            /> 
                        ))
                    }
                </div>
            </Loader>
        </>  
    );
}

export default ReviewsPage;