package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EmailException extends ApiException {
    public EmailException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
