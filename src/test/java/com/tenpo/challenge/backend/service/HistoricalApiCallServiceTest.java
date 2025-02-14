package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.entity.ApiCallLog;
import com.tenpo.challenge.backend.repository.ApiCallLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoricalApiCallServiceTest {

    @Mock
    private ApiCallLogRepository repository;

    @InjectMocks
    private HistoricalApiCallService historicalApiCallService;

    @Captor
    private ArgumentCaptor<ApiCallLog> apiCallLogCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ TEST 1: Guarda una llamada a la API exitosamente
    @Test
    void shouldSaveApiCallLogSuccessfully() {
        historicalApiCallService.save("/test", "param1=123", "Success", 200);

        verify(repository, times(1)).save(apiCallLogCaptor.capture());

        ApiCallLog savedLog = apiCallLogCaptor.getValue();
        assertEquals("/test", savedLog.getEndpoint());
        assertEquals("param1=123", savedLog.getParameters());
        assertEquals("Success", savedLog.getResponse());
        assertEquals(200, savedLog.getStatusCode());
    }

    // ✅ TEST 2: Maneja excepciones al guardar una llamada
    @Test
    void shouldHandleExceptionWhenSavingApiCallLog() {
        doThrow(new RuntimeException("Mocked DataBase error")).when(repository).save(any(ApiCallLog.class));

        assertDoesNotThrow(() -> historicalApiCallService.save("/error", "param=fail", "Failure", 500));

        verify(repository, times(1)).save(any(ApiCallLog.class));
    }

    // ✅ TEST 3: Devuelve llamadas almacenadas con paginación
    @Test
    void shouldReturnAllApiCallLogsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ApiCallLog> mockLogs = List.of(
                new ApiCallLog("/history", "param=value", "Response", 200)
        );

        Page<ApiCallLog> mockPage = new PageImpl<>(mockLogs, pageable, mockLogs.size());
        when(repository.findAll(pageable)).thenReturn(mockPage);

        Page<ApiCallLog> result = historicalApiCallService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("/history", result.getContent().get(0).getEndpoint());
        verify(repository, times(1)).findAll(pageable);
    }
}
