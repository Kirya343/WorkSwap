"use client"

import Card from "@/components/ui/Card/Card";
import Loader from "@core/components/common/Loader/Loader";
import Breadcrumbs from "@core/components/ui/Breadcrumbs/Breadcrumbs";
import Pagination from "@core/components/ui/Pagination/Pagination";
import { Page } from "@core/lib/common/types/page";
import { IReview } from "@core/lib/review/types";
import styles from "./ReviewsPage.module.scss"
import { reviewsService } from "@core/lib/review/reviewsService"
import { useCallback, useEffect, useState } from "react";
import ReviewItem from "@core/components/ui/reviews/ReviewItem/ReviewItem";

const ReviewsPage = () => {

    const [reviews, setReviews] = useState<Page<IReview> | null>(null);

    const loadReviews = useCallback(async (page: number) => {
        const data: Page<IReview> = await reviewsService.getReviewsPage(page, 10, "id");
        setReviews(data);
        console.log(data)
    }, [])
    
    useEffect(() => {
        loadReviews(0);
    }, [])

    return (
        <>
            <Breadcrumbs
                crumbs={[
                    { href: "/dashboard", title: "Панель управления" },
                    { href: "#", title: "Управление отзывами" },
                ]}
            />
            <Card>
                <Loader loadingActive={!reviews?.content}>
                    <div className={styles.grid}>
                        {reviews?.content?.map(review => (
                            <ReviewItem key={review.id} review={review}/>
                        ))}
                    </div>
                    <Pagination
                        page={reviews?.page.number || 0} 
                        totalPages={reviews?.page.totalPages || 1} 
                        onChange={(page) => loadReviews(page)}
                    />
                </Loader>
            </Card>
        </>
    );
};

export default ReviewsPage;