export const Languages = Object.freeze({
    FI: { code: "fi", label: "Suomi" },
    EN: { code: "en", label: "English" },
    RU: { code: "ru", label: "Русский" },
    IT: { code: "it", label: "Italiano" }
});

export const DEFAULT_LOCALE = 'en'

// Также можно создать массив для перебора
export const languagesList = Object.values(Languages);
export const supportedLanguages = Object.values(Languages).map(lang => lang.code);

export const locales = ['en', 'fi', 'ru', 'it'] as const;
export type Locale = (typeof locales)[number];

export function parseLocale(input: string): Locale {
    return locales.includes(input as Locale) ? (input as Locale) : 'en';
}