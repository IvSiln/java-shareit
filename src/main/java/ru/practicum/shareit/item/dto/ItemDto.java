package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ru.practicum.shareit.validation.ValidationType.Create;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    Long id;

    @NotBlank(groups = Create.class, message = "Название не может быть пустым")
    String name;

    @NotBlank(groups = Create.class, message = "Описание не может быть пустым")
    @Size(max = 200, message = "Длина описания должна до 200 символов")
    String description;

    @NotNull(groups = Create.class, message = "Поле доступности вещи не может быть пустым")
    Boolean available;

    Long requestId;
}
