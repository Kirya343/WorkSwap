"use client"

import { createContext, Dispatch, ReactNode, SetStateAction, useContext, useState } from "react"
import { ICatalogFilters } from "@core/lib/common/types/catalog";

type CatalogFiltersContextType = {
    filters: ICatalogFilters;
    updateFilter: (key: string, value: string | boolean | number | null) => void;
    totalPages: number;
    setTotalPages: Dispatch<SetStateAction<number>>;
}

export const CatalogFiltersContext = createContext<CatalogFiltersContextType | undefined>(undefined)

export function useCatalogFilters() {
    const context = useContext(CatalogFiltersContext)
    if (!context) throw new Error("useFilters must be used inside FiltersProvider")
    return context
}

interface CatalogFiltersProviderProps {
    initialFilters: ICatalogFilters; 
    initialTotalPages: number;
    children: ReactNode; 
}

export function CatalogFiltersProvider({ initialFilters, initialTotalPages, children }: CatalogFiltersProviderProps) {

    const [totalPages, setTotalPages] = useState<number>(initialTotalPages || 1);
    const [filters, setFilters] = useState<ICatalogFilters>(initialFilters);

    function updateFilter(key: string, value: string | number | boolean | null) {
        console.log("обновляем фильтр", key, value)
        setFilters(prev => ({ ...prev, [key]: value }));

        const params = new URLSearchParams(window.location.search)

        if (
            value === null ||
            value === "" ||
            value === false ||
            value === undefined
        ) {
            params.delete(key)
        } else {
            params.set(key, String(value))
        }

        const query = params.toString()

        const newUrl = query
            ? `${window.location.pathname}?${query}`
            : window.location.pathname

        window.history.replaceState(null, "", newUrl)
    }

    return (
        <CatalogFiltersContext.Provider value={{ filters, updateFilter, totalPages, setTotalPages }}>
            {children}
        </CatalogFiltersContext.Provider>
    )
}
