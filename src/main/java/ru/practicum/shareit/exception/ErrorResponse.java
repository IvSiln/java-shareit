package ru.practicum.shareit.exception;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {
    String error;

    Map<String, String> validationErrors;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
