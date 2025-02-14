package com.tenpo.challenge.backend.controller;

import com.tenpo.challenge.backend.entity.ApiCallLog;
import com.tenpo.challenge.backend.filter.RateLimitFilter;
import com.tenpo.challenge.backend.service.HistoricalApiCallService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoricalApiCallController.class) // 🔹 Test unitario del controlador
class HistoricalApiCallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoricalApiCallService historicalApiCallService;

    @MockBean
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setup() throws ServletException, IOException {
        ApiCallLog log1 = new ApiCallLog("/calculate", "num1=10&num2=5", "200 OK", 200);
        ApiCallLog log2 = new ApiCallLog("/calculate", "num1=20&num2=-10", "400 BAD_REQUEST", 400);

        List<ApiCallLog> logList = List.of(log1, log2);
        Page<ApiCallLog> mockApiCallLogs = new PageImpl<>(logList, PageRequest.of(0, 10), logList.size());

        Mockito.when(historicalApiCallService.getAll(any(Pageable.class))).thenReturn(mockApiCallLogs);

        doAnswer(invocation ->
                     {
                     FilterChain chain = invocation.getArgument(2);
                     chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
                     return null;
                     })
                .when(rateLimitFilter)
                .doFilter(any(), any(), any());
    }

    @Test
    void shouldReturnHistoryWhenExists() throws Exception {
        mockMvc.perform(get("/history")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // ✅ Esperamos 200 OK
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].endpoint").value("/calculate"))
                .andExpect(jsonPath("$[0].parameters").value("num1=10&num2=5"))
                .andExpect(jsonPath("$[0].response").value("200 OK"))
                .andExpect(jsonPath("$[0].statusCode").value("200"));
    }

    @Test
    void shouldReturnEmptyListWhenNoHistoryExists() throws Exception {
        when(historicalApiCallService.getAll(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/history")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // ✅ Esperamos 200 OK
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldSupportPagination() throws Exception {
        mockMvc.perform(get("/history")
                                .param("page", "0")
                                .param("size", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // ✅ Esperamos 200 OK
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ❌ TEST 4: Maneja errores internos correctamente
    @Test
    void shouldReturnInternalServerErrorWhenServiceFails() throws Exception {
        when(historicalApiCallService.getAll(any())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/history")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.cause").value("Unexpected error"));
    }

}
