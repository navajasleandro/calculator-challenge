package com.tenpo.challenge.backend.controller.dto;

public record ErrorResponseDTO(int status, String error, String cause) {

}
