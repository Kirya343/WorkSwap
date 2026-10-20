import { IShortUser } from "../user/types";

export interface IReview {
    id: number;
    text: string;
    rating: number;
    author: IShortUser;
    profileSub: string | null;
    listingId: number | null;
    createdAt: string;
}