package com.tenpo.challenge.backend.controller;

import com.tenpo.challenge.backend.service.PercentageService;
import com.tenpo.challenge.backend.service.PercentageService.PercentageServiceResponse;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PercentageService percentageService;

    @BeforeEach
    void setup() {
        Mockito.when(percentageService.calculate(anyDouble(), anyDouble()))
                .thenReturn(new PercentageServiceResponse(10.0, 110.0));
    }

    @Test
    void shouldReturnCalculationResultWhenValidInputs() throws Exception {
        mockMvc.perform(get("/calculate")
                                .param("num1", "90")
                                .param("num2", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.num1").value(90))
                .andExpect(jsonPath("$.num2").value(10))
                .andExpect(jsonPath("$.percentage").value(10.0))
                .andExpect(jsonPath("$.result").value(110.0));
    }

    @Test
    void shouldReturnBadRequestWhenNum1IsNegative() throws Exception {
        mockMvc.perform(get("/calculate")
                                .param("num1", "-10")
                                .param("num2", "50")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNum1IsZero() throws Exception {
        mockMvc.perform(get("/calculate")
                                .param("num1", "0")
                                .param("num2", "50")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNum1IsMissing() throws Exception {
        mockMvc.perform(get("/calculate")
                                .param("num2", "50")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNum2IsMissing() throws Exception {
        mockMvc.perform(get("/calculate")
                                .param("num1", "50")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
