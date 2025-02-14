package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.client.PercentageApiClient;
import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import com.tenpo.challenge.backend.exception.PercentageCacheServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PercentageCacheService {
    private final Cache cache;
    private final Cache hisoricalCache;
    public static final String PERCENTAGE_KEY = "percentage";
    private final PercentageApiClient percentageApiClient;

    public PercentageCacheService(CacheManager cacheManager, PercentageApiClient percentageApiClient) {
        this.cache = cacheManager.getCache("percentageCache");
        this.hisoricalCache = cacheManager.getCache("percentageHistoricalCache");
        this.percentageApiClient = percentageApiClient;
    }

    public double getCachedPercentage() {
        Double cachedValue = cache.get(PERCENTAGE_KEY, Double.class);

        if (cachedValue != null) {
            log.info("Using cached percentage value: {}", cachedValue);
            return cachedValue;
        }

        try {
            log.info("Fetching new percentage value from external API");
            double newPercentage = percentageApiClient.getPercentage();
            cache.put(PERCENTAGE_KEY, newPercentage);
            hisoricalCache.put(PERCENTAGE_KEY, newPercentage);
            return newPercentage;
        } catch (PercentageApiClientException e) {
            Double historicalValue = hisoricalCache.get(PERCENTAGE_KEY, Double.class);
            if (historicalValue != null) {
                return historicalValue;
            }
            throw new PercentageCacheServiceException("Failed to get percentage value from external API and no historical value found");
        }
    }
}
