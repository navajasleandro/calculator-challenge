package com.tenpo.challenge.backend.controller.dto;

import com.tenpo.challenge.backend.entity.ApiCallLog;

import java.time.LocalDateTime;

public record HistoricalDTO(
        LocalDateTime timestamp,
        String endpoint,
        String parameters,
        String response,
        Integer statusCode) {

    public static HistoricalDTO from(ApiCallLog apiCallLog) {
        return new HistoricalDTO(
                apiCallLog.getTimestamp()
                , apiCallLog.getEndpoint()
                , apiCallLog.getParameters()
                , apiCallLog.getResponse()
                , apiCallLog.getStatusCode()
        );
    }
}
