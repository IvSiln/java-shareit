package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    Long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 512, message = "Комментарий не может быть длиннее 512 знаков")
    String text;
    String authorName;
    LocalDateTime created;
}
