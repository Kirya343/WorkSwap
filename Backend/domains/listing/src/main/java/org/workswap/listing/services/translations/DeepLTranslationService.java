package org.workswap.listing.services.translations;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.workswap.listing.dto.ListingTranslationDTO;

@Service
@Profile("server")
public class DeepLTranslationService {

    private final WebClient webClient;

    @Value("${deepl.api-key}")
    private String apiKey;

    private static final String MODEL_TYPE = "quality_optimized";

    public DeepLTranslationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api-free.deepl.com")
                .build();
    }

    public ListingTranslationDTO translate(ListingTranslationDTO dto, String targetLanguage, String sourceLanguage) {
        DeepLResponse response = webClient.post()
                .uri("/v2/translate")
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "text", List.of(dto.title(), dto.description()),
                        "target_lang", targetLanguage,
                        "source_lang", sourceLanguage,
                        "model_type", MODEL_TYPE
                ))
                .retrieve()
                .bodyToMono(DeepLResponse.class)
                .block();

        if (response == null || response.translations() == null || response.translations().isEmpty()) {
            throw new IllegalStateException("DeepL returned an empty response");
        }

        return new ListingTranslationDTO(
            response.translations().get(0).text(), 
            response.translations().get(1).text()
        );
    }

    public record DeepLResponse(
            List<Translation> translations
    ) {}

    public record Translation(
            String detected_source_language,
            String text
    ) {}
}