package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ErrorResponseTest {

    @Test
    void testErrorMessageConstructor() {
        String error = "Error message";
        ErrorResponse response = new ErrorResponse(error);

        assertEquals(error, response.getError());
        assertNull(response.getValidationErrors());
    }

    @Test
    void testValidationErrorsConstructor() {
        Map<String, String> validationErrors = new HashMap<>();
        validationErrors.put("field1", "Error 1");
        validationErrors.put("field2", "Error 2");

        ErrorResponse response = new ErrorResponse(validationErrors);

        assertNull(response.getError());
        assertEquals(validationErrors, response.getValidationErrors());
    }
}
