import { IReview } from '@core/lib/review/types';
import { apiFetch, apiFetchJson } from '@core/lib/common/utils/apiClient';

export const reviewsService = {
    getReviewslist: (params: {listingId?: number, profileId?: number}) => apiFetchJson('/review/list', {}, params),
    getReviewsPage: (page: number, size: number, sortParam: string) => apiFetchJson('/review/page', {}, { page, size, sortParam }),
    createReview: (newReview: IReview) => apiFetch(`/review`, { 
        method: 'POST', 
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(newReview),
    }),
    getMyReviews: () => apiFetchJson(`/review/my`)
}