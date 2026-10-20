export interface Page<T> {
    content: T[];
    page: {
        totalPages: number;
        totalElements: number;
        number: number;          // текущая страница (0-based)
        size: number;
        first: boolean;
        last: boolean;
    }
}

export interface IPageRequest {
    page: number;
    size: number;
}