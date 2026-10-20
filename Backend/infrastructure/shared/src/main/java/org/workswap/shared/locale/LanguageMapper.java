package org.workswap.shared.locale;

import com.github.pemistahl.lingua.api.Language;

public class LanguageMapper {
    public static String toShortCode(Language language) {
        switch (language) {
            case RUSSIAN:
                return "ru";
            case ENGLISH:
                return "en";
            case FINNISH:
                return "fi";
            default:
                return "unknown";
        }
    }
}