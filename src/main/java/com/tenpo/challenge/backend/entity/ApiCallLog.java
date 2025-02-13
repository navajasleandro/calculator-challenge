package com.tenpo.challenge.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_log")
@Getter
@Setter
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private Integer statusCode;

    public ApiCallLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiCallLog(String endpoint, String parameters, String response, Integer statusCode) {
        this.timestamp = LocalDateTime.now();
        this.endpoint = endpoint;
        this.parameters = parameters;
        this.response = response;
        this.statusCode = statusCode;
    }
}
