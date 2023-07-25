package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemRequestTest {

    @Test
    void testEqualsAndHashCode() {
        // Create two item requests with the same id
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);

        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);

        // Create another item request with a different id
        ItemRequest request3 = new ItemRequest();
        request3.setId(2L);

        // Test equals method
        assertEquals(request1, request2); // Objects with the same id should be considered equal
        assertNotEquals(request1, request3); // Objects with different ids should not be considered equal

        // Test hashCode method
        assertEquals(request1.hashCode(), request2.hashCode()); // Objects with the same id should have the same hash code
        assertNotEquals(request1.hashCode(), request3.hashCode()); // Objects with different ids should have different hash codes
    }
}
