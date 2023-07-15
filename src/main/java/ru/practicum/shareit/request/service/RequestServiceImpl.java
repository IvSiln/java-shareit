package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.SortUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private static final Sort SORT = SortUtil.DESCENDING_SORT_BY_CREATED;

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestNewDto add(Long userId, ItemRequestNewDto itemRequestNewDto) {
        User requester = findUserById(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestNewDto);
        itemRequest.setRequester(requester);
        itemRequest = requestRepository.save(itemRequest);
        return RequestMapper.toItemRequestNewDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        findUserById(userId);
        List<ItemRequestDto> itemRequestDto = requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        addItemsToRequests(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findAll(long userId, int from, int size) {
        findUserById(userId);
        PageRequest page = PageRequest.of(from / size, size, SORT);
        List<ItemRequestDto> itemRequestDto = requestRepository.findByRequesterIdNot(userId, page)
                .map(RequestMapper::toItemRequestDto)
                .getContent();
        addItemsToRequests(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        findUserById(userId);
        ItemRequest itemRequest = findRequestById(requestId);
        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        requestDto.addAllItems(items);
        return requestDto;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    private ItemRequest findRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
    }

    private void addItemsToRequests(List<ItemRequestDto> itemRequestDto) {
        List<Long> requestIds = itemRequestDto.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<ItemDto> itemDto = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        if (itemDto.isEmpty()) {
            return;
        }

        Map<Long, ItemRequestDto> requests = new HashMap<>();
        Map<Long, List<ItemDto>> items = new HashMap<>();

        itemDto.forEach(dto -> items.computeIfAbsent(dto.getRequestId(), key -> new ArrayList<>()).add(dto));
        itemRequestDto.forEach(request -> requests.put(request.getId(), request));
        items.forEach((key, value) -> requests.get(key).addAllItems(value));
    }
}