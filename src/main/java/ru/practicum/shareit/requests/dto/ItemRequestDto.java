package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private final List<ItemDto> items = new ArrayList<>();
    private long id;
    private String description;
    private LocalDateTime created;

    public ItemRequestDto() {
        // Default constructor
    }

    public ItemRequestDto(long id, String description, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }

    public void addAllItems(List<ItemDto> itemsToAdd) {
        items.addAll(itemsToAdd);
    }
}
