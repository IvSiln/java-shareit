package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestNewDto {
    long id;

    @NotBlank
    @Size(max = 200, message = "Description must not exceed {max} characters")
    private String description;

    private LocalDateTime created;
}


