package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.enums.Status.APPROVED;
import static ru.practicum.shareit.booking.enums.Status.WAITING;

class ItemBookingCommentsDtoTest {

    @Test
    void testAddComment() {
        // Создаем объекты CommentDto
        CommentDto comment1 = CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .authorName("Author 1")
                .created(LocalDateTime.now())
                .build();

        CommentDto comment2 = CommentDto.builder()
                .id(2L)
                .text("Comment 2")
                .authorName("Author 2")
                .created(LocalDateTime.now())
                .build();

        // Создаем объект ItemBookingCommentsDto
        ItemBookingCommentsDto item = ItemBookingCommentsDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .build();

        // Добавляем комментарии к объекту ItemBookingCommentsDto
        item.addComment(comment1);
        item.addComment(comment2);

        // Проверяем, что комментарии были добавлены корректно
        List<CommentDto> comments = item.getComments();
        assertEquals(2, comments.size());
        assertEquals(comment1, comments.get(0));
        assertEquals(comment2, comments.get(1));
    }

    @Test
    void testLastBookingAndNextBooking() {
        // Создаем объекты BookingResponseDto
        BookingResponseDto lastBooking = BookingResponseDto.builder()
                .id(1L)
                .status(APPROVED)
                .build();

        BookingResponseDto nextBooking = BookingResponseDto.builder()
                .id(2L)
                .status(WAITING)
                .build();

        // Создаем объект ItemBookingCommentsDto с последним и следующим бронированием
        ItemBookingCommentsDto item = ItemBookingCommentsDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();

        // Проверяем, что последнее и следующее бронирование установлены корректно
        assertEquals(lastBooking, item.getLastBooking());
        assertEquals(nextBooking, item.getNextBooking());
    }
}
