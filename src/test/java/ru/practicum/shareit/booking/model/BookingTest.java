package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    @Test
    public void testId() {
        Booking booking = new Booking();
        Long id = 1L;
        booking.setId(id);

        assertEquals(id, booking.getId());
    }

    @Test
    public void testStart() {
        Booking booking = new Booking();
        Instant start = Instant.now();
        booking.setStart(start);

        assertEquals(start, booking.getStart());
    }

    @Test
    public void testEnd() {
        Booking booking = new Booking();
        Instant end = Instant.now();
        booking.setEnd(end);

        assertEquals(end, booking.getEnd());
    }

    @Test
    public void testItem() {
        Booking booking = new Booking();
        Item item = new Item();
        booking.setItem(item);

        assertEquals(item, booking.getItem());
    }

    @Test
    public void testBooker() {
        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);

        assertEquals(booker, booking.getBooker());
    }

    @Test
    public void testStatus() {
        Booking booking = new Booking();
        Status status = Status.APPROVED;
        booking.setStatus(status);

        assertEquals(status, booking.getStatus());
    }
}
