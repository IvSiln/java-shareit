package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {

    @Test
    void testEqualsAndHashCode() {
        // Create two items with the same id
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        // Create another item with a different id
        Item item3 = new Item();
        item3.setId(2L);

        // Test equals method
        assertEquals(item1, item2); // Objects with the same id should be considered equal
        assertNotEquals(item1, item3); // Objects with different ids should not be considered equal

        assertEquals(item1.hashCode(), item2.hashCode()); // Objects with the same id should have the same hash code
        assertNotEquals(item1.hashCode(), item3.hashCode()); // Objects with different ids should have different hash codes    }
    }
}
