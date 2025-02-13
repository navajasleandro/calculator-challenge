package com.tenpo.challenge.backend.client;

import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class PercentageApiClient {
    private static final Logger log = LoggerFactory.getLogger(PercentageApiClient.class);

    public static final String URL_SERVICE_MOCKED = "/v3/f170be6e-50c0-458c-a428-f6e7528fa5e2";
    public static final double DEFAULT_VALUE = 10L;
    private final WebClient webClient;

    public PercentageApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://run.mocky.io").build();
    }


    public Double getPercentage() {
        log.info("[API CALL] Getting percentage from external service");
        return this.webClient.get()
                .uri(URL_SERVICE_MOCKED)
                .retrieve()
                .bodyToMono(PercentageResponse.class)
                .map(PercentageResponse::percentage)
                .retry(3)
                .doOnError(ex -> log.error("API call failed after 3 retries: {}", ex.getMessage()))
                .blockOptional()
                .orElseThrow(() -> new PercentageApiClientException("External API failed after 3 retries"));
    }

    public record PercentageResponse(double percentage) {}
}
