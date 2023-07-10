package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;

import java.util.List;

public interface RequestService {
    ItemRequestNewDto add(Long userId, ItemRequestNewDto itemRequestNewDto);

    List<ItemRequestDto> findAllByUserId(Long userId);

    List<ItemRequestDto> findAll(long userId, int from, int size);

    ItemRequestDto findById(long userId, long requestId);
}
