package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CommentMapperTest {
    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    @Test
    void testToCommentDto() {
        LocalDateTime created = LocalDateTime.of(2023, 7, 10, 10, 10, 10);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("some text");
        User author = new User();
        author.setId(2L);
        author.setName("Test_User2");
        author.setEmail("mail2@somemail.ru");
        comment.setAuthor(author);
        comment.setCreated(created.toInstant(ZONE_OFFSET));

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        Assertions.assertEquals(created, commentDto.getCreated());
    }

    @Test
    void testToComment() {
        LocalDateTime created = LocalDateTime.of(2023, 7, 10, 10, 10, 10);
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("some text")
                .authorName("Test_User2")
                .created(created)
                .build();

        Comment comment = CommentMapper.toComment(commentDto);

        Assertions.assertEquals(commentDto.getText(), comment.getText());
    }
}
