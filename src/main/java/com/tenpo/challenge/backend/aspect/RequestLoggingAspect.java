package com.tenpo.challenge.backend.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.backend.controller.dto.ErrorResponseDTO;
import com.tenpo.challenge.backend.service.HistoricalApiCallService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
@Aspect
@Component
public class RequestLoggingAspect {
    private final ObjectMapper objectMapper;
    private final HistoricalApiCallService historicalApiCallService;

    @Before("execution(* com.tenpo.challenge.backend.controller.*.*(..))")
    public void logInboundRequest(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = getUrl(request);
        log.info(
                "{}.{}() --> Inbound request URL: {} {} {}"
                , joinPoint.getTarget().getClass().getName()
                , joinPoint.getSignature().getName()
                , request.getMethod()
                , url
                , this.getRequestBody(joinPoint).orElse(""));
    }

    @AfterReturning(pointcut = "execution(* com.tenpo.challenge.backend.controller.CalculationController.*(..))", returning = "result")
    public void logOutboundResponse(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String responseBody = result.toString();
        int statusCode = 200;

        if (result instanceof ResponseEntity<?> responseEntity) {
            statusCode = responseEntity.getStatusCode().value();
            try {
                responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
            } catch (Exception e) {
                log.error("Error while JSON body serializes: (after controller execution)" + e.getMessage());
            }
        }

        this.historicalApiCallService.save(
                request.getRequestURL().toString(),
                request.getQueryString(),
                responseBody,
                statusCode
        );
    }

    @AfterReturning(pointcut = "execution(* com.tenpo.challenge.backend.exception.GlobalExceptionHandler.*(..))", returning = "responseEntity")
    public void logOutboundExceptionResponse(JoinPoint joinPoint, ResponseEntity<ErrorResponseDTO> responseEntity) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String responseBodyJson = "";
        try {
            responseBodyJson = objectMapper.writeValueAsString(responseEntity.getBody());
        } catch (JsonProcessingException e) {
            log.error("Error while JSON body serializes: (after globalExceptionHandler execution)" + e.getMessage());
        }

        this.historicalApiCallService.save(
                request.getRequestURL().toString(),
                request.getQueryString(),
                responseBodyJson,
                responseEntity.getStatusCode().value()
        );
    }


    private static String getUrl(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String formattedParams = queryString != null ? "?" + queryString : "";
        return request.getRequestURL().toString() + formattedParams;
    }

    private Optional<String> getRequestBody(JoinPoint thisJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) thisJoinPoint.getSignature();
        Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
        if (0 == annotationMatrix.length) {
            return Optional.empty();
        }

        Object[] args = thisJoinPoint.getArgs();
        return IntStream.range(0, annotationMatrix.length)
                .mapToObj(index -> {
                List<Annotation> annotations = Arrays.asList(annotationMatrix[index]);
                if (annotations.stream()
                        .anyMatch(RequestBody.class::isInstance)) {
                    try {
                        return this.objectMapper
                                .writeValueAsString(args[index]);
                    } catch (Exception e) {
                        log.error("Error while JSON body serializes (before controller execution): " + e.getMessage());
                        return null;
                    }
                }
                return null;
                })
                .filter(Objects::nonNull)
                .findFirst();
    }
}
