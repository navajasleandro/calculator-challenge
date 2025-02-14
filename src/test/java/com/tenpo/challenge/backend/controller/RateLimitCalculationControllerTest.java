package com.tenpo.challenge.backend.controller;

import com.tenpo.challenge.backend.service.PercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
class RateLimitCalculationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PercentageService percentageService;

    @BeforeEach
    void setup() {
        Mockito.when(percentageService.calculate(anyDouble(), anyDouble()))
                .thenReturn(new PercentageService.PercentageServiceResponse(10.0, 110.0));
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimitExceeded() throws Exception {
        mockMvc.perform(get("/calculate").param("num1", "10").param("num2", "5")
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mockMvc.perform(get("/calculate").param("num1", "10").param("num2", "5")
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        mockMvc.perform(get("/calculate").param("num1", "10").param("num2", "5")
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        // 🔹 La cuarta request debe fallar con 429 Too Many Requests
        mockMvc.perform(get("/calculate")
                                .param("num1", "10")
                                .param("num2", "5")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());
    }
}
