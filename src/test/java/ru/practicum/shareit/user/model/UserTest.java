package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        // Create two users with the same id
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        // Create another user with a different id
        User user3 = new User();
        user3.setId(2L);

        // Test equals method
        assertEquals(user1, user2); // Objects with the same id should be considered equal
        assertNotEquals(user1, user3); // Objects with different ids should not be considered equal
        assertEquals(user1.hashCode(), user2.hashCode()); // Objects with the same id should have the same hash code
        assertNotEquals(user1.hashCode(), user3.hashCode()); // Objects with different ids should have different hash codes
    }
}
