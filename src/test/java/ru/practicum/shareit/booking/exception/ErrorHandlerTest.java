package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class ErrorHandlerTest {

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertEquals("Resource not found", response.getError());
        assertNull(response.getValidationErrors());
    }

//    @Test
//    void testHandleValidationExceptions() {
//        ConstraintViolationException exception = mock(ConstraintViolationException.class);
//        ErrorHandler errorHandler = new ErrorHandler();
//        ErrorResponse response = errorHandler.handleValidationExceptions(exception);
//
//        assertNotNull(response.getError());
//        assertNull(response.getValidationErrors());
//    }
//
//    @Test
//    void testHandleMethodArgumentTypeMismatchException() {
//        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
//        ErrorHandler errorHandler = new ErrorHandler();
//        ErrorResponse response = errorHandler.handleMethodArgumentTypeMismatchException(exception);
//
//        assertNotNull(response.getError());
//        assertNull(response.getValidationErrors());
//    }
//
//    @Test
//    void testHandleMethodArgumentNotValid() {
//        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
//        ErrorHandler errorHandler = new ErrorHandler();
//        ErrorResponse response = errorHandler.handleMethodArgumentNotValid(exception);
//
//        assertNotNull(response.getError());
//        assertNotNull(response.getValidationErrors());
//    }
//
//    @Test
//    void testHandleMissingRequestHeaderException() {
//        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);
//        ErrorHandler errorHandler = new ErrorHandler();
//        ErrorResponse response = errorHandler.handleMissingRequestHeaderException(exception);
//
//        assertNotNull(response.getError());
//        assertNull(response.getValidationErrors());
//    }

    @Test
    void testHandleThrowable() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorResponse response = errorHandler.handleThrowable();

        assertNotNull(response.getError());
        assertNull(response.getValidationErrors());
    }
}
