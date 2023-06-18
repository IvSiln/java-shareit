package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class PermissionException extends ApiException {
    public PermissionException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
