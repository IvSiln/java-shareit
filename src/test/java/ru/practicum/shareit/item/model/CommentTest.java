package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentTest {

    @Test
    public void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();
        comment3.setId(2L);

        assertTrue(comment1.equals(comment2)); // Проверка на равенство объектов с одинаковыми id
        assertFalse(comment1.equals(comment3)); // Проверка на неравенство объектов с разными id
    }
}