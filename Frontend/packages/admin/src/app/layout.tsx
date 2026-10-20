import { ReactNode } from "react";
import "@/css/main.scss";
import { headers } from "next/headers";

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

export default async function RootLayout({
    children
}: {
    children: ReactNode;
}) {
    const loadedHeaders = await headers();
    const theme = loadedHeaders.get("x-theme") || "light";

    return (
        <html lang="ru" data-theme={theme} data-scroll-behavior="smooth">
            <head>
                <link rel="icon" href="/favicon.png" />
            </head>
            <body>
                {children}
            </body>
        </html>
    );
}