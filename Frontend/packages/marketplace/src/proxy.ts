import { DEFAULT_LOCALE, supportedLanguages } from '@core/lib/common/constants/languages';
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const PUBLIC_FILE = /\.(.*)$/;

export function proxy(request: NextRequest) {
    const { pathname } = request.nextUrl;

    if (
        pathname.startsWith('/_next') ||
        pathname.startsWith('/api') ||
        PUBLIC_FILE.test(pathname)
    ) {
        return;
    }

    // Разбиваем путь
    const segments = pathname.split('/');
    const firstSegment = segments[1];

    // Если корень — редирект на дефолтную локаль
    if (!firstSegment) {
        const cookieLocale = request.cookies.get("locale")?.value;

        const locale =
            cookieLocale && supportedLanguages.includes(cookieLocale)
                ? cookieLocale
                : DEFAULT_LOCALE;

        return NextResponse.redirect(
            new URL(`/${locale}`, request.url)
        );
    }

    // Если локаль не поддерживается — редиректим на дефолтную
    if (!supportedLanguages.includes(firstSegment)) {
        const cookieLocale = request.cookies.get("locale")?.value;

        const locale =
            cookieLocale && supportedLanguages.includes(cookieLocale)
                ? cookieLocale
                : DEFAULT_LOCALE;

        return NextResponse.redirect(
            new URL(`/${locale}${pathname}`, request.url)
        );
    }
    const theme = request.cookies.get('theme')?.value || 'light';

    const response = NextResponse.next();
    response.headers.set('x-theme', theme);

    return response;
}

export const config = {
    matcher: ['/:path*']
};