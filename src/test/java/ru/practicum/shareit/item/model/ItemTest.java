package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(item1.equals(item2)); // Objects with the same id should be considered equal
        assertFalse(item1.equals(item3)); // Objects with different ids should not be considered equal
    }
}
