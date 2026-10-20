import { apiFetch, apiFetchJson, apiFetchText } from '@core/lib/common/utils/apiClient';
import { ICategory } from '../types';

export const getPathToCategory = (categoryId: number, listingType: string) => apiFetchJson(`/category/${listingType.toLowerCase()}/${categoryId}/path`);
export const getAllCategories = () => apiFetchJson("/category/all");
export const getCategoriesByType = (listingType: string) => apiFetchJson(`/category/${listingType.toLowerCase()}/all`)

export const createCategory = (category: ICategory, listingType: string) => 
    apiFetchText(
        `/category/${listingType.toLowerCase()}`, 
        { 
            method: "POST", 
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(category) 
        }
    )
export const deleteCategory = (categoryId: number, listingType: string) => 
    apiFetch(
        `/category/${listingType.toLowerCase()}/${categoryId}`,
        { method: "DELETE" }
    )