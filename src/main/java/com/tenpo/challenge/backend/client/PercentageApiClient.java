package com.tenpo.challenge.backend.client;

import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;


@Service
public class PercentageApiClient {
    private static final Logger log = LoggerFactory.getLogger(PercentageApiClient.class);
    public static final String URL_SERVICE_MOCKED = "/v3/f170be6e-50c0-458c-a428-f6e7528fa5e2";
    private final WebClient webClient;

    public PercentageApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Double getPercentage() {
        log.info("[API CALL] Getting percentage from external service");
        return this.webClient.get()
                .uri(URL_SERVICE_MOCKED)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response
                                .bodyToMono(PercentageResponse.class)
                                .map(PercentageResponse::percentage);
                    } else {
                        log.warn("API call failed with status: {}", response.statusCode());
                        return Mono.error(new PercentageApiClientException("External API failed with status: " + response.statusCode()));
                    }
                })
                .retry(3)
                .onErrorResume(PercentageApiClientException.class, ex -> {
                    log.error("Final error after retries: {}", ex.getMessage());
                    return Mono.error(new PercentageApiClientException("External API failed after 3 retries"));
                })
                .blockOptional()
                .orElseThrow(() -> new PercentageApiClientException("External API failed after 3 retries"));
    }

    public record PercentageResponse(double percentage) {}
}

