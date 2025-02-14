package com.tenpo.challenge.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.tenpo.challenge.backend.service.PercentageService.PercentageServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PercentageServiceTest {

    @Mock
    private PercentageCacheService percentageCacheService;

    @InjectMocks
    private PercentageService percentageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateWithPositiveNumbers() {
        when(percentageCacheService.getCachedPercentage()).thenReturn(10.0);

        PercentageServiceResponse response = percentageService.calculate(100.0, 50.0);

        assertEquals(10.0, response.percentageApplied());
        assertEquals(165.0, response.result());
    }

    @Test
    void calculateWithZeroValues() {
        when(percentageCacheService.getCachedPercentage()).thenReturn(10.0);

        PercentageServiceResponse response = percentageService.calculate(0.0, 0.0);

        assertEquals(10.0, response.percentageApplied());
        assertEquals(0.0, response.result());
    }

    @Test
    void calculateWithNegativeNumbers() {
        when(percentageCacheService.getCachedPercentage()).thenReturn(10.0);

        PercentageServiceResponse response = percentageService.calculate(-100.0, -50.0);

        assertEquals(10.0, response.percentageApplied());
        assertEquals(-165.0, response.result());
    }

    @Test
    void calculateWithMixedSignNumbers() {
        when(percentageCacheService.getCachedPercentage()).thenReturn(10.0);

        PercentageServiceResponse response = percentageService.calculate(100.0, -50.0);

        assertEquals(10.0, response.percentageApplied());
        assertEquals(55.0, response.result());
    }

    @Test
    void calculateWithZeroPercentage() {
        when(percentageCacheService.getCachedPercentage()).thenReturn(0.0);

        PercentageServiceResponse response = percentageService.calculate(100.0, 50.0);

        assertEquals(0.0, response.percentageApplied());
        assertEquals(150.0, response.result());
    }
}
