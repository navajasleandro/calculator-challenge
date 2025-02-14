package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.client.PercentageApiClient;
import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import com.tenpo.challenge.backend.exception.PercentageCacheServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PercentageCacheServiceTest {

    private Cache cache;
    private Cache historicalCache;
    private PercentageApiClient percentageApiClient;
    private PercentageCacheService percentageCacheService;

    @BeforeEach
    void setUp() {
        // 🔹 Mock de CacheManager y sus cachés
        CacheManager cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        historicalCache = mock(Cache.class);
        percentageApiClient = mock(PercentageApiClient.class);

        when(cacheManager.getCache("percentageCache")).thenReturn(cache);
        when(cacheManager.getCache("percentageHistoricalCache")).thenReturn(historicalCache);

        percentageCacheService = new PercentageCacheService(cacheManager, percentageApiClient);
    }

    @Test
    void shouldReturnCachedPercentageWhenAvailable() {
        when(cache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(20.0);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(20.0, result);
        verify(cache, never()).put(anyString(), anyDouble());
        verify(percentageApiClient, never()).getPercentage();
    }

    @Test
    void shouldFetchNewPercentageWhenCacheIsEmpty() {
        when(cache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenReturn(15.5);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(15.5, result);
        verify(cache).put(PercentageCacheService.PERCENTAGE_KEY, 15.5);
        verify(historicalCache).put(PercentageCacheService.PERCENTAGE_KEY, 15.5);
    }

    @Test
    void shouldReturnHistoricalValueWhenApiFails() {
        when(cache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenThrow(new PercentageApiClientException("API error"));
        when(historicalCache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(30.0);

        double result = percentageCacheService.getCachedPercentage();

        assertEquals(30.0, result);
    }

    @Test
    void shouldThrowExceptionWhenApiFailsAndNoHistoricalValueExists() {
        when(cache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(null);
        when(percentageApiClient.getPercentage()).thenThrow(new PercentageApiClientException("API error"));
        when(historicalCache.get(PercentageCacheService.PERCENTAGE_KEY, Double.class)).thenReturn(null);

        assertThrows(PercentageCacheServiceException.class, () -> percentageCacheService.getCachedPercentage());
    }
}
