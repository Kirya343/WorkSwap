export const ListingPublicType = {
    SERVICE_OFFER: 'SERVICE_OFFER',
    SERVICE_REQUEST: 'SERVICE_REQUEST',
    PRODUCT_SALE: 'PRODUCT_SALE',
    PRODUCT_PURCHASE: 'PRODUCT_PURCHASE',
    PRODUCT_SWAP: 'PRODUCT_SWAP',
    PRODUCT_GIVEAWAY: 'PRODUCT_GIVEAWAY',
    PRODUCT_WANTED_FREE: 'PRODUCT_WANTED_FREE',
    EVENT: 'EVENT',
} as const;

export const ListingType = {
    SERVICE: 'SERVICE',
    PRODUCT: 'PRODUCT',
    EVENT: 'EVENT',
} as const;


export type ListingPublicTypeValue =
    typeof ListingPublicType[keyof typeof ListingPublicType];

export type ListingTypeValue =
    typeof ListingType[keyof typeof ListingType];


export const listingPublicTypes = Object.values(ListingPublicType).map(
    (key) => ({ key })
);

export const listingTypesWithRating: ListingTypeValue[] = [
    ListingType.EVENT,
    ListingType.SERVICE,
];