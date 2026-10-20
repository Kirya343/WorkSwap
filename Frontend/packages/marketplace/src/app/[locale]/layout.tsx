import { ReactNode } from "react";
import { getDictionary } from "@/lib/i18n";
import { parseLocale } from '@core/lib/common/constants/languages';
import Header from "@/components/layout/header/Header/Header";
import { AppProviders } from "@core/AppProviders"

import "@/css/main.scss";
import "@/css/globals.scss";
import styles from "./RootLayout.module.scss";
import { I18nProvider } from "@core/lib/common/contexts/I18nContext";

export const metadata = {
    title: "WorkSwap",
};

export function generateStaticParams() {
    return [
        { locale: 'en' },
        { locale: 'ru' },
        { locale: 'fi' }
    ];
}

export default async function LocaleLayout({ children, params}: { children: ReactNode; params: Promise<{ locale: string }>;}) {

    const { locale } = await params;
    const parsed = parseLocale(locale)
    const dict = await getDictionary(parsed);

    return (
        <I18nProvider locale={parsed} dict={dict}>
            <AppProviders>
                <div className={styles.root}>
                    <Header />
                    {children}

                    {/* <LanguageSelectModal /> */}
                </div>

                <div id="modal-root"></div>
                <div id="mobile-menu"></div>
            </AppProviders>
        </I18nProvider>
    );
}