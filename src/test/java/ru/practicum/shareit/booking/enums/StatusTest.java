package ru.practicum.shareit.booking.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest {

    @Test
    void enumValues_ContainAllStatuses() {
        Status[] statuses = Status.values();

        assertEquals(4, statuses.length);
        assertEquals(Status.WAITING, statuses[0]);
        assertEquals(Status.APPROVED, statuses[1]);
        assertEquals(Status.REJECTED, statuses[2]);
        assertEquals(Status.CANCELED, statuses[3]);
    }

    @Test
    void valueOf_ReturnsCorrectStatus() {
        Status status = Status.valueOf("APPROVED");

        assertEquals(Status.APPROVED, status);
    }

    @Test
    void toString_ReturnsStatusName() {
        String statusName = Status.REJECTED.toString();

        assertEquals("REJECTED", statusName);
    }
}
