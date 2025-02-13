package com.tenpo.challenge.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("percentageCache",
                                         Caffeine.newBuilder()
                                                 .expireAfterWrite(30, TimeUnit.MINUTES)
                                                 .maximumSize(1)
                                                 .build()
        );

        cacheManager.registerCustomCache("percentageHistoricalCache",
                                         Caffeine.newBuilder()
                                                 .expireAfterWrite(7, TimeUnit.DAYS)
                                                 .maximumSize(1)
                                                 .build()
        );

        return cacheManager;
    }

}
