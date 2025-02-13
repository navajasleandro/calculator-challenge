package com.tenpo.challenge.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PercentageService {
    private final PercentageCacheService percentageCacheService;

    public PercentageService(PercentageCacheService percentageCacheService) {
        this.percentageCacheService = percentageCacheService;
    }

    public PercentageServiceResponse calculate(double first, double second) {
        double sum = first + second;
        double percentage = this.percentageCacheService.getCachedPercentage();
        double result = sum + (sum * (percentage / 100));
        return new PercentageServiceResponse(percentage, result);
    }

    public record PercentageServiceResponse(double percentageApplied, double result) {}
}
