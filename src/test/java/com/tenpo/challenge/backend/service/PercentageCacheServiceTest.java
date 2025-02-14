package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.client.PercentageApiClient;
import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import com.tenpo.challenge.backend.exception.PercentageCacheServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static com.tenpo.challenge.backend.utils.ApiConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PercentageCacheServiceTest {

    private Cache shortTermCache;
    private Cache longTermCache;
    private PercentageApiClient percentageApiClient;
    private PercentageCacheService percentageCacheService;

    @BeforeEach
    void setUp() {
        // 🔹 Mock de CacheManager y sus cachés
        CacheManager cacheManager = mock(CacheManager.class);
        shortTermCache = mock(Cache.class);
        longTermCache = mock(Cache.class);
        percentageApiClient = mock(PercentageApiClient.class);

        when(cacheManager.getCache(PERCENTAGE_SHORT_TERM_CACHE)).thenReturn(shortTermCache);
        when(cacheManager.getCache(PERCENTAGE_LONG_TERM_CACHE)).thenReturn(longTermCache);

        percentageCacheService = new PercentageCacheService(cacheManager, percentageApiClient);
    }

    @Test
    void shouldReturnCachedPercentageWhenAvailable() {
        when(shortTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(20.0);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(20.0, result);
        verify(shortTermCache, never()).put(anyString(), anyDouble());
        verify(percentageApiClient, never()).getPercentage();
    }

    @Test
    void shouldFetchNewPercentageWhenCacheIsEmpty() {
        when(shortTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenReturn(15.5);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(15.5, result);
        verify(shortTermCache).put(PERCENTAGE_KEY, 15.5);
        verify(longTermCache).put(PERCENTAGE_KEY, 15.5);
    }

    @Test
    void shouldReturnHistoricalValueWhenApiFails() {
        when(shortTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenThrow(new PercentageApiClientException("API error"));
        when(longTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(30.0);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(30.0, result);
    }

    @Test
    void shouldThrowExceptionWhenApiFailsAndNoHistoricalValueExists() {
        when(shortTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenThrow(new PercentageApiClientException("API error"));
        when(longTermCache.get(PERCENTAGE_KEY, Double.class)).thenReturn(null);

        assertThrows(PercentageCacheServiceException.class, () -> percentageCacheService.getCachedPercentage());
    }
}
