package com.tenpo.challenge.backend.service;

import com.tenpo.challenge.backend.entity.ApiCallLog;
import com.tenpo.challenge.backend.repository.ApiCallLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HistoricalApiCallService {
    private static final Logger log = LoggerFactory.getLogger(HistoricalApiCallService.class);
    private final ApiCallLogRepository repository;

    public HistoricalApiCallService(ApiCallLogRepository apiCallLogRepository) {
        this.repository = apiCallLogRepository;
    }

    @Async
    public void save(String endpoint, String parameters, String response, Integer statusCode) {
        try {
            ApiCallLog logEntry = new ApiCallLog(endpoint, parameters, response, statusCode);
            repository.save(logEntry);
        } catch (Exception e) {
            log.error("Error while saving API call log", e);
        }
    }

    public Page<ApiCallLog> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
