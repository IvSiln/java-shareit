package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlerTest {

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals("Resource not found", response.getError());
        assertNull(response.getValidationErrors());
    }

    @Test
    void testHandleThrowable() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleThrowable();

        assertNotNull(response.getError());
        assertNull(response.getValidationErrors());
    }
}
