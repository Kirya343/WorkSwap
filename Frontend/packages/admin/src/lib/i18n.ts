import { Locale } from '@core/lib/common/constants/languages';

const dictionaries = {
    en: () => import('./dictionaries/en').then(m => m.default),
    fi: () => import('./dictionaries/fi').then(m => m.default),
    ru: () => import('./dictionaries/ru').then(m => m.default),
    it: () => import('./dictionaries/it').then(m => m.default),
}

export const getDictionary = async (locale: Locale) => {
    const loader = dictionaries[locale] ?? dictionaries.en
    return loader()
}