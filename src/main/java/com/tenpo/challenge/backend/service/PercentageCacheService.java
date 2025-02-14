package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.client.PercentageApiClient;
import com.tenpo.challenge.backend.exception.PercentageApiClientException;
import com.tenpo.challenge.backend.exception.PercentageCacheServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static com.tenpo.challenge.backend.utils.ApiConstants.*;

@Slf4j
@Service
public class PercentageCacheService {
    private final Cache shortTermCache;
    private final Cache longTermCache;
    private final PercentageApiClient percentageApiClient;

    public PercentageCacheService(CacheManager cacheManager, PercentageApiClient percentageApiClient) {
        this.shortTermCache = cacheManager.getCache(PERCENTAGE_SHORT_TERM_CACHE);
        this.longTermCache = cacheManager.getCache(PERCENTAGE_LONG_TERM_CACHE);
        this.percentageApiClient = percentageApiClient;
    }

    public double getCachedPercentage() {
        Double cachedValue = shortTermCache.get(PERCENTAGE_KEY, Double.class);

        if (cachedValue != null) {
            return cachedValue;
        }

        try {
            double newPercentage = percentageApiClient.getPercentage();
            shortTermCache.put(PERCENTAGE_KEY, newPercentage);
            longTermCache.put(PERCENTAGE_KEY, newPercentage);
            return newPercentage;
        } catch (PercentageApiClientException e) {
            Double historicalValue = longTermCache.get(PERCENTAGE_KEY, Double.class);
            if (historicalValue != null) {
                return historicalValue;
            }
            throw new PercentageCacheServiceException("Failed to get percentage value from external API and no historical value found");
        }
    }
}
