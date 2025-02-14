package com.tenpo.challenge.backend.exception;

import com.tenpo.challenge.backend.controller.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PercentageApiClientException.class)
    public ResponseEntity<ErrorResponseDTO> handleApiClientException(PercentageApiClientException ex) {
        log.error("Cannot retrieve percentage value from external API. Error message: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Cannot retrieve percentage value from external API.");
    }

    @ExceptionHandler(PercentageCacheServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleCacheServiceException(PercentageCacheServiceException ex) {
        log.error("Not found in cache: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Cannot retrieve percentage value from cache.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("HTTP error: {} - {}", ex.getStatusCode(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.resolve(ex.getStatusCode()
                                           .value()), ex.getReason()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameterException(
            MissingServletRequestParameterException ex
    ) {
        log.warn("Missing parameter: {}", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Wrong parameter value: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Wrong parameter value: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, String cause) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponseDTO(status.value(), status.getReasonPhrase(), cause));
    }

}
