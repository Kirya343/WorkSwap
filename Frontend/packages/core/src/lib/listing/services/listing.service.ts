import { ICatalogFilters } from "@core/lib/common/types/catalog"
import { apiFetch, apiFetchJson, apiFetchText } from "@core/lib/common/utils/apiClient"
import { IListingTranslation, IListingUpdate } from "@core/lib/listing/types";

export const getListingAccessToken = (listingId: number) => apiFetchJson(`/listing/${listingId}/token`, {method: "GET"});

export const getPageById = (listingId: number) => apiFetchJson(`/listing/${listingId}/page`);
export const getById = (listingId: number, token?: string) => apiFetchJson(`/listing/${listingId}`, {method: "GET"}, {token});

export const getImages = (listingId: number) => apiFetchJson(`/listing/${listingId}/images`);
export const getTranslations = (listingId: number) => apiFetchJson(`/listing/${listingId}/translations`);
export const getCatalog = (params: ICatalogFilters) => 
        apiFetchJson(`/listing/catalog`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(params)
        });

export const getFavorites = () => apiFetchJson('/listing/favorites');
export const getMyListings = () => apiFetchJson('/listing/my-listings');
export const getDrafts = () => apiFetchJson("/listing/drafts");
export const getByUserId = (userId: number) => apiFetchJson(`/listing/by-user`, {}, {userId});
export const getListingsPage = (page: number, amount: number, sortParam: string) => 
    apiFetchJson(`/listing/page`, {}, {page, amount, sortParam});
export const getRecentListings = (amount: number) => apiFetchJson(`/listing/recent`, {}, {amount});

export const deleteListing = (listingId: number) => apiFetch(`/listing/${listingId}`, {method: 'DELETE'});
export const create = (type: string) => apiFetchText(`/listing`, {method: "PUT"}, {type});
export const publish = (listingId: number) => apiFetch(`/listing/${listingId}/publish`, {method: 'POST'});

export const update = (listingId: number, updates: IListingUpdate) => 
        apiFetch(`/listing/${listingId}/update`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updates),
        });

export const modifyTranslations = (listingId: number, translations: IListingTranslation) => 
        apiFetchJson(`/listing/${listingId}/modify/translations`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(translations),
        });
export const autoTranslate = (listingId: number, lang: string, preferedRefLang: string) => 
    apiFetchJson(`/listing/${listingId}/auto-translate`, {method: 'POST'}, { lang, preferedRefLang })

export const checkFavorite = (listingId: number) => apiFetchJson(`/listing/${listingId}/favorite`, {method: 'GET'});
export const addFavorite = (listingId: number) => apiFetch(`/listing/${listingId}/favorite`, {method: 'POST'});
export const removeFavorite = (listingId: number) => apiFetch(`/listing/${listingId}/favorite`, {method: 'DELETE'});

export const uploadListingImage = (listingId: number, formData: FormData) => 
        apiFetchJson(`/listing/${listingId}/image`, {
            method: "POST",
            body: formData
        }, {
            listingId
        });

export const deleteListingImage = (listingId: number, imageId: number) => 
        apiFetchJson(`/listing/${listingId}/image/${imageId}`, { method: "DELETE" }, {});