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
    long id;
    private String description;
    private LocalDateTime created;
    private final List<ItemDto> items = new ArrayList<>();

    public void addAllItems(List<ItemDto> itemsToAdd) {
        items.addAll(itemsToAdd);
    }
}