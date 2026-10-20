"use client"

import { IReview } from "@core/lib/review/types";
import ReviewForm from "./ReviewForm/ReviewForm";
import ReviewsList from "./ReviewsList/ReviewsList";
import { useEffect, useState } from "react";
import { reviewsService } from "@core/lib/review/reviewsService";

interface ReviewsSectionProps {
    listingId: number | null;
    profileSub: string | null;
}

const ReviewsSection = ({listingId, profileSub}: ReviewsSectionProps) => {

    const [reviews, setReviews] = useState<IReview[] | null>(null);

    useEffect(() => {
        if (!profileSub) return;
        const params: {profileSub: string, listingId: number | undefined} = {profileSub: profileSub, listingId: undefined};
        console.log("listingId: ", listingId)
        if (listingId) params.listingId = listingId;

        async function loadReviews() {
            if (!params.listingId) return;
            const data = await reviewsService.getReviewslist(params);
            setReviews(await data);
        }

        loadReviews();
    }, [listingId, profileSub])

    if (!profileSub) return;

    return (
        <>
            <ReviewForm setReviews={setReviews} listingId={listingId} profileSub={profileSub} />
            {reviews && reviews.length > 0 && (
                <ReviewsList reviews={reviews} />
            )}
        </>
    );
};

export default ReviewsSection;