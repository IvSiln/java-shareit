package ru.practicum.shareit.requests.dto;

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
    @Size(max = 200)
    private String description;
    private LocalDateTime created;
}
