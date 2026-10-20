package org.workswap.shared.locale;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.pemistahl.lingua.api.Language;

import java.util.List;

@Configuration
public class LocalisationConfig implements WebMvcConfigurer {

    public class LanguageUtils {
        public static final List<String> SUPPORTED_LANGUAGES = List.of("ru", "fi", "en", "it");
        public static final Language[] SUPPORTED_LANGUAGES_LINGUA = {
            Language.RUSSIAN,
            Language.ENGLISH,
            Language.FINNISH,
            Language.ITALIAN
        };
    }
}


