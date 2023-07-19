package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    @Test
    void testEqualsAndHashCode() {
        // Create two Booking objects with the same attributes
        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStart(Instant.now());
        booking1.setEnd(Instant.now().plusSeconds(3600));
        Item item1 = new Item();
        item1.setId(1L);
        booking1.setItem(item1);
        User booker1 = new User();
        booker1.setId(1L);
        booking1.setBooker(booker1);
        booking1.setStatus(Status.APPROVED);

        Booking booking2 = new Booking();
        booking2.setId(1L);
        booking2.setStart(booking1.getStart());
        booking2.setEnd(booking1.getEnd());
        Item item2 = new Item();
        item2.setId(1L);
        booking2.setItem(item2);
        User booker2 = new User();
        booker2.setId(1L);
        booking2.setBooker(booker2);
        booking2.setStatus(Status.APPROVED);

        // Verify that the two objects are equal and have the same hash code
        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());

        // Change one attribute and verify that the objects are no longer equal
        booking2.setStatus(Status.REJECTED);
        assertNotEquals(booking1, booking2);
    }
}
