"use client"

import { useAuth } from "@core/lib/auth/AuthContext";
import { useI18n } from "@core/lib/common/contexts/I18nContext";
import { useNotification } from "@core/lib/notification/NotificationContext";
import { Dispatch, SetStateAction, useState } from "react";
import { IReview } from "@core/lib/review/types"
import { reviewsService } from "@core/lib/review/reviewsService"
import styles from "./ReviewForm.module.scss"
import StarIcon from "@core/components/common/icons/StarIcon";

interface ReviewFormProps {
    setReviews: Dispatch<SetStateAction<IReview[] | null>>;
    listingId: number | null;
    profileSub: string;
}

const ReviewForm = ({setReviews, listingId, profileSub}: ReviewFormProps) => {

    const { dict } = useI18n();

    const {user, isAuthenticated} = useAuth();
    const { notificate } = useNotification();

    const [text, setText] = useState<string>('');
    const [rating, setRating] = useState<number>(0);   // выбранная оценка
    const [hover, setHover] = useState<number>(0);     // звезда под курсором

    const createR = async () => {

        if (!user) return; 

        if (text.length === 0) {
            notificate(dict.messages.notification.misc.nullReviewText, "error");
            return;
        }

        if (rating === 0) {
            notificate(dict.messages.notification.misc.nullReviewRating, "error");
            return;
        }

        const newReview: IReview = {
            id: 0,
            text,
            rating,
            author: { sub: user.sub, name: user.name, avatarUrl: user.avatarUrl },
            listingId,
            profileSub,
            createdAt: new Date().toISOString()
        };
        
        const res = await reviewsService.createReview(newReview);

        if (res.ok) {
            notificate(dict.messages.notification.success.reviewCreate, "success");
            newReview.id = Date.now();
            setReviews(prev => {
                if (!prev) return prev;
                return [newReview, ...prev]
            });
            setText('');
            setRating(0);
        }
    };

    return (
        <>
            {(isAuthenticated && profileSub && profileSub != user?.sub) && (
                <section className={styles.form}>
                    <h3>{dict.common.reviews.item.label}</h3>
                    <div className={styles.formRow}>
                        <div className={styles.ratingGroup}>
                            <label htmlFor="ratingStars">{dict.common.reviews.item.rating}</label>
                            <div className={styles.rating} id="ratingStars">
                                {[1, 2, 3, 4, 5].map((star) => {
                                    const isFilled = rating >= star;
                                    const isHalf = !isFilled && rating >= star - 0.5;

                                    return (
                                        <button
                                            key={star}
                                            onClick={() => setRating(star)}
                                            onMouseEnter={() => setHover(star)}
                                            onMouseLeave={() => setHover(0)}
                                        >
                                            <StarIcon
                                                filled={isFilled}
                                                half={isHalf}
                                                className={`
                                                    ${styles.star}
                                                    ${hover >= star
                                                        ? styles.hovered
                                                        : isFilled
                                                        ? styles.selected
                                                        : ""
                                                    }
                                                `}
                                            />
                                        </button>
                                    );
                                })}
                            </div>
                        </div>
                        <div className={styles.textGroup}>
                            <label htmlFor="text">{dict.common.reviews.item.text}</label>
                            <textarea 
                                id="text" 
                                value={text}
                                onChange={(e) => setText(e.target.value)}
                                placeholder={dict.common.placeholders.review}
                            >
                            </textarea>
                        </div>
                    </div>
                    <button 
                        onClick={createR}
                        className="btn btn-primary"
                    >
                        {dict.buttons.review.create}
                    </button>
                </section>
            )}
        </>
    );
};

export default ReviewForm;