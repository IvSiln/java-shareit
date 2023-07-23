package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingResponseDtoTest {

    @Test
    public void testGettersAndSetters() {
        long id = 1L;
        LocalDateTime start = LocalDateTime.of(2023, 7, 13, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 7, 13, 12, 0);
        long bookerId = 2L;
        Status status = Status.WAITING;

        BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
                .id(id)
                .start(start)
                .end(end)
                .bookerId(bookerId)
                .status(status)
                .build();

        assertEquals(id, bookingResponseDto.getId());
        assertEquals(start, bookingResponseDto.getStart());
        assertEquals(end, bookingResponseDto.getEnd());
        assertEquals(bookerId, bookingResponseDto.getBookerId());
        assertEquals(status, bookingResponseDto.getStatus());
    }
}

