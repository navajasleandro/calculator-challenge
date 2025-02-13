package com.tenpo.challenge.backend.controller;

import com.tenpo.challenge.backend.controller.dto.ResponseDTO;
import com.tenpo.challenge.backend.service.PercentageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Calculation")
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/calculate")
public class CalculationController {

    private final PercentageService percentageService;

    @Operation(summary = "Calculate",
               description = "Endpoint to calculate the sum of two numbers and apply a percentage obtained from an external API")
    @GetMapping
    public ResponseEntity<ResponseDTO> calculate(
            @Parameter(description = "First number. Must be positive") @RequestParam @Positive Double num1
            , @Parameter(description = "Second number. Must be positive") @RequestParam @Positive Double num2
    ) {
        PercentageService.PercentageServiceResponse response = percentageService.calculate(num1, num2);
        ResponseDTO responseDTO = new ResponseDTO(num1, num2, response.percentageApplied(), response.result());
        return new ResponseEntity<>(responseDTO, null, HttpStatus.OK);
    }

}
