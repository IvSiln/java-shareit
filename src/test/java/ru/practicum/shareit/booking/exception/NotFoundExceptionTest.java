package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotFoundExceptionTest {

    @Test
    public void testConstructorAndGetMessage() {
        String message = "Resource not found";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
    }
}
