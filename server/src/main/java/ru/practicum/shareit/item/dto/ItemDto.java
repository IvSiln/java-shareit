package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
}
