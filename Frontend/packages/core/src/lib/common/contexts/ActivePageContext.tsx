"use client"

import { createContext, useContext } from "react";
import { ReactNode, useMemo } from 'react';
import { usePathname } from "next/navigation";

export const pageTitles = {
    dashboard: "Панель управления",
    listings: "Управление объявлениями",
    reviews: "Управление отзывами",
    users: "Управление пользователями",
    news: "Управление новостями",
    locations: "Управление локациями",
    categories: "Управление категориями",
    tasks: "Задачи",
    localization: "Локализация",
    permissions: "Роли и разрешения",
} as const;

type PageKey = keyof typeof pageTitles;

const ActivePageContext = createContext<PageKey | null>(null);

export const useActivePage = () => {
    const ctx = useContext(ActivePageContext);
    if (ctx === undefined) {
        throw new Error("useActivePage must be used inside ActivePageProvider");
    }
    return ctx;
}

export const ActivePageProvider = ({ children }: {children: ReactNode}) => {

    const pathname = usePathname();

    const mapping: Record<string, PageKey> = {
        dashboard: "dashboard",
        listings: "listings",
        reviews: "reviews",
        users: "users",
        locations: "locations",
        categories: "categories",
        tasks: "tasks",
        localization: "localization",
        permissions: "permissions",
    };

    const activePage = useMemo<PageKey | null>(() => {
        const segments = pathname.split("/").filter(Boolean);

        const pageSegment = segments[1];

        return pageSegment && pageSegment in mapping
            ? mapping[pageSegment]
            : null;
    }, [pathname]);

    return (
        <ActivePageContext.Provider value={activePage}>
            {children}
        </ActivePageContext.Provider>
    );
};