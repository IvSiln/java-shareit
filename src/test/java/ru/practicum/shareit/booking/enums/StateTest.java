package ru.practicum.shareit.booking.enums;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.State;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateTest {

    @Test
    void enumValues_ContainAllStates() {
        State[] states = State.values();

        assertEquals(7, states.length);
        assertEquals(State.ALL, states[0]);
        assertEquals(State.CURRENT, states[1]);
        assertEquals(State.PAST, states[2]);
        assertEquals(State.FUTURE, states[3]);
        assertEquals(State.WAITING, states[4]);
        assertEquals(State.REJECTED, states[5]);
        assertEquals(State.UNSUPPORTED_STATUS, states[6]);
    }

    @Test
    void valueOf_ReturnsCorrectState() {
        State state = State.valueOf("CURRENT");

        assertEquals(State.CURRENT, state);
    }

    @Test
    void toString_ReturnsStateName() {
        String stateName = State.PAST.toString();

        assertEquals("PAST", stateName);
    }
}
