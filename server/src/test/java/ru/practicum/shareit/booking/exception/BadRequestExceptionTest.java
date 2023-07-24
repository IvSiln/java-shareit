package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadRequestExceptionTest {

    @Test
    public void testConstructor() {
        String message = "Bad request";
        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
    }
}
