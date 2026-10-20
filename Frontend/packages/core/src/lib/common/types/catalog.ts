export interface ICatalogFilters {
    categoryId?: number;
    searchQuery?: string;
    hasReviews?: boolean;
    translationsFilter?: boolean;
    sortBy?: string;
    type?: string;
    page: number;
}