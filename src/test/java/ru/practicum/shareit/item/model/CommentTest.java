package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    public void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();
        comment3.setId(2L);

        assertEquals(comment1, comment2); // Проверка на равенство объектов с одинаковыми id
        assertNotEquals(comment1, comment3); // Проверка на неравенство объектов с разными id

        assertEquals(comment1.hashCode(), comment2.hashCode()); // Проверка на равенство hashCode для объектов с одинаковыми id
        assertFalse(comment1.hashCode() == comment3.hashCode()); // Проверка на неравенство hashCode для объектов с разными id
    }
}