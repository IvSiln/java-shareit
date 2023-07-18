package ru.practicum.shareit.validatio;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.validation.Validation;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ValidationImplTest {

    @Test
    void testCheckNotBlank() {
        // Test with a non-blank string
        String nonBlankString = "Hello";
        assertDoesNotThrow(() -> Validation.checkNotBlank(nonBlankString, "Parameter"));

        // Test with a blank string
        String blankString = "";
        ValidationException exception = assertThrows(ValidationException.class,
                () -> Validation.checkNotBlank(blankString, "Parameter"));
        assertEquals("Parameter не может быть пустым", exception.getMessage());
    }
}
