import { apiFetchJson } from '@core/lib/common/utils/apiClient';

export const settingService = {
    getSupportedLanguages: () => apiFetchJson("/settings/languages"),
    getSupportedPriceTypes: () => apiFetchJson('/settings/price-types')
}