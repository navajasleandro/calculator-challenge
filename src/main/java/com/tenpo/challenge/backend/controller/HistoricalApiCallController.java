package com.tenpo.challenge.backend.controller;

import com.tenpo.challenge.backend.controller.dto.HistoricalDTO;
import com.tenpo.challenge.backend.service.HistoricalApiCallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@Tag(name = "History")
@AllArgsConstructor
@RestController
@RequestMapping("/history")
public class HistoricalApiCallController {

    private final HistoricalApiCallService historicalApiCallService;

    @Operation(summary = "Get history", description = "Endpoint to get the history of API calls")
    @GetMapping
    public ResponseEntity<Stream<HistoricalDTO>> getHistory(@ParameterObject Pageable pageable) {
        Page<HistoricalDTO> all = this.historicalApiCallService.getAll(pageable)
                .map(HistoricalDTO::from);
        return new ResponseEntity<>(all.get(), null, HttpStatus.OK);
    }
}
