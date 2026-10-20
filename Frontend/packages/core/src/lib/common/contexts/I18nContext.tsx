"use client"

import { createContext, useContext, ReactNode } from "react";
import { Locale } from "../constants/languages";

export type Dict = Record<string, any>; // уточни по структуре словаря

interface I18nContextProps {
    locale: Locale;
    dict: Dict;
}

const I18nContext = createContext<I18nContextProps | null>(null);

export const I18nProvider = ({ locale, dict, children }: { locale: Locale, dict: Dict, children: ReactNode }) => {
    return (
        <I18nContext.Provider value={{ locale, dict }}>
            {children}
        </I18nContext.Provider>
    );
};

export const useI18n = () => {
    const context = useContext(I18nContext);
    if (!context) throw new Error("useI18n must be used within I18nProvider");
    return context;
};