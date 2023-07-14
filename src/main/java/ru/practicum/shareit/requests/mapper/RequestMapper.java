package ru.practicum.shareit.requests.mapper;


import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class RequestMapper {
    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        LocalDateTime created = LocalDateTime.ofInstant(itemRequest.getCreated(), ZONE_OFFSET);
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(created)
                .build();
    }

    public static ItemRequestNewDto toItemRequestNewDto(ItemRequest itemRequest) {
        LocalDateTime created = LocalDateTime.ofInstant(itemRequest.getCreated(), ZONE_OFFSET);
        return ItemRequestNewDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(created)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestNewDto itemRequestNewDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestNewDto.getDescription());
        return itemRequest;
    }
}