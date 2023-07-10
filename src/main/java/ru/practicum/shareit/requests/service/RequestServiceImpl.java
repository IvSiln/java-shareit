package ru.practicum.shareit.requests.service;

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
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;
import ru.practicum.shareit.requests.mapper.RequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    private final ItemRequestRepository repository;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    @Override
    @Transactional
    public ItemRequestNewDto add(Long userId, ItemRequestNewDto itemRequestNewDto) {
        User requestor = checkUser(userId);
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestNewDto);
        itemRequest.setRequestor(requestor);
        itemRequest = repository.save(itemRequest);
        return RequestMapper.toItemRequestNewDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Long userId) {
        checkUser(userId);
        List<ItemRequestDto> itemRequestDtos = repository.findByRequestorId(userId).stream()
                .map(RequestMapper::toItemRequestDto).collect(Collectors.toList());
        addItemsToRequests(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> findAll(long userId, int from, int size) {
        checkUser(userId);
        PageRequest page = PageRequest.of(from / size, size, SORT);
        List<ItemRequestDto> itemRequestDtos = repository.findByRequestorIdNot(userId, page)
                .map(RequestMapper::toItemRequestDto).getContent();
        addItemsToRequests(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto findById(long userId, long requestId) {
        checkUser(userId);
        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id %d не найден", requestId)));
        ItemRequestDto requestDto = RequestMapper.toItemRequestDto(itemRequest);
        List<ItemDto> items = itemRepo.findByRequestId(requestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        requestDto.addAllItems(items);
        return requestDto;
    }

    private User checkUser(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    private void addItemsToRequests(List<ItemRequestDto> itemRequestDtos) {
        List<Long> requestIds = itemRequestDtos.stream().map(ItemRequestDto::getId).collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepo.findByRequestIdIn(requestIds).stream()
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