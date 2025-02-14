package com.tenpo.challenge.backend.client;

import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PercentageApiClientTest {

    private static MockWebServer mockWebServer;
    private static PercentageApiClient percentageApiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient.Builder webClientBuilder = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString());

        percentageApiClient = new PercentageApiClient(webClientBuilder);
    }



    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnPercentageWhenApiResponds() {
        // 🔹 Simula una respuesta JSON exitosa
        mockWebServer.enqueue(new MockResponse()
                                      .setBody("{\"percentage\": 15.5}")
                                      .addHeader("Content-Type", "application/json"));

        Double percentage = percentageApiClient.getPercentage();

        assertNotNull(percentage);
        assertEquals(15.5, percentage);
    }

    @Test
    void shouldReturnPercentageAfterOneApiFail() {
        // 🔹 Simula un error 500 en la API
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        mockWebServer.enqueue(new MockResponse()
                                      .setBody("{\"percentage\": 15.5}")
                                      .addHeader("Content-Type", "application/json"));

        Double percentage = percentageApiClient.getPercentage();
        assertNotNull(percentage);
        assertEquals(15.5, percentage);
    }

    @Test
    void shouldReturnPercentageAfterTwoApiFails() {
        // 🔹 Simula un error 500 en la API
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        mockWebServer.enqueue(new MockResponse()
                                      .setBody("{\"percentage\": 15.5}")
                                      .addHeader("Content-Type", "application/json"));

        Double percentage = percentageApiClient.getPercentage();
        assertNotNull(percentage);
        assertEquals(15.5, percentage);
    }

    @Test
    void shouldReturnPercentageAfterTreeApiFails() {
        // 🔹 Simula un error 500 en la API
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        mockWebServer.enqueue(new MockResponse()
                                      .setBody("{\"percentage\": 15.5}")
                                      .addHeader("Content-Type", "application/json"));

        Double percentage = percentageApiClient.getPercentage();
        assertNotNull(percentage);
        assertEquals(15.5, percentage);
    }

    @Test
    void shouldRetryThreeTimesBeforeFailing() {
        // 🔹 Simula 3 respuestas fallidas (HTTP 500)
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
                                      .setBody("{\"percentage\": 15.5}")
                                      .addHeader("Content-Type", "application/json"));

        PercentageApiClientException exception = assertThrows(PercentageApiClientException.class, () -> {
        percentageApiClient.getPercentage();
        });

        assertEquals(4, mockWebServer.getRequestCount(), "API should be called 4 times (one call & 3 retries)");
        assertEquals("External API failed after 3 retries", exception.getMessage());
    }
}
