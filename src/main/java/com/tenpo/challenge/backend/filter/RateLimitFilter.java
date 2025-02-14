package com.tenpo.challenge.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenpo.challenge.backend.controller.dto.ErrorResponseDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter implements Filter {
    private static final int RATE_LIMIT = 3;
    private final Bucket bucket;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitFilter() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(RATE_LIMIT)
                .refillGreedy(RATE_LIMIT, Duration.ofMinutes(1))
                .build();

        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write(
                    objectMapper.writeValueAsString(
                        new ErrorResponseDTO(
                                HttpStatus.TOO_MANY_REQUESTS.value()
                                , "Too Many Requests"
                                , "Rate limit exceeded. Try again later.")
                    )
            );
        }
    }
}
