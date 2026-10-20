import { ReactNode } from "react";
import "@/css/main.scss";
import Layout from "@/components/layout/Layout/Layout";
import { parseLocale } from '@core/lib/common/constants/languages';
import { getDictionary } from "@/lib/i18n";
import { I18nProvider } from "@core/lib/common/contexts/I18nContext";
import { AppProviders } from "@core/AppProviders"

export const metadata = {
    title: "WorkSwap Dashboard",
};

export function generateStaticParams() {
    return [
        { locale: 'en' },
        { locale: 'ru' },
        { locale: 'fi' }
    ];
}

export default async function RootLayout({ children, params }: { children: ReactNode; params: Promise<{ locale: string }>;}) {

    const { locale } = await params;
    const parsed = parseLocale(locale)
    const dict = await getDictionary(parsed);

    return (
        <I18nProvider locale={parsed} dict={dict}>
            <AppProviders>
                <Layout>
                    {children}
                </Layout>
            </AppProviders>
        </I18nProvider>
    );
}