package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }


    @Override
    @Transactional
    public ItemRequestNewDto add(Long userId, ItemRequestNewDto itemRequestNewDto) {
        UserDto requester = userService.findById(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestNewDto);
        itemRequest.setRequester(UserMapper.toUser(requester));
        itemRequest = requestRepository.save(itemRequest);
        return RequestMapper.toItemRequestNewDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        userService.findById(userId);
        List<ItemRequestDto> itemRequestDto = requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toItemRequestDto).collect(Collectors.toList());
        addItemsToRequests(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> findAll(long userId, int from, int size) {
        userService.findById(userId);
        PageRequest page = PageRequest.of(from / size, size, SORT);
        List<ItemRequestDto> itemRequestDtos = requestRepository.findByRequesterIdNot(userId, page)
                .map(RequestMapper::toItemRequestDto).getContent();
        addItemsToRequests(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        userService.findById(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        requestDto.addAllItems(items);
        return requestDto;
    }

    private void addItemsToRequests(List<ItemRequestDto> itemRequestDtos) {
        List<Long> requestIds = itemRequestDtos.stream().map(ItemRequestDto::getId).collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());

        if (itemDtos.isEmpty()) {
            return;
        }
        Map<Long, ItemRequestDto> requests = new HashMap<>();
        Map<Long, List<ItemDto>> items = new HashMap<>();

        itemDtos.forEach(itemDto -> items.computeIfAbsent(itemDto.getRequestId(), key -> new ArrayList<>()).add(itemDto));
        itemRequestDtos.forEach(request -> requests.put(request.getId(), request));
        items.forEach((key, value) -> requests.get(key).addAllItems(value));
    }
}