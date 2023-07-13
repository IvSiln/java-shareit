package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.RequestRepository;
import ru.practicum.shareit.requests.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    RequestServiceImpl requestService;

    private User requester;
    private User owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        requester = new User();
        requester.setName("name2");
        requester.setEmail("e2@mail.ru");
        requester.setId(2L);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequester(requester);
        request.setCreated(Instant.now());
        request.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
    }


    @Test
    void add() {
        //Regular case
        when(requestRepository.save(any())).thenReturn(request);
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        ItemRequestNewDto requestDto = requestService.add(requester.getId(),
                ItemRequestNewDto.builder().description("description").build());
        assertNotNull(requestDto);
        assertEquals(request.getId(), requestDto.getId());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void addFailByUserNotFound() {
        //Fail By User Not Found
        long userNotFoundId = 0L;
        String error = String.format("Пользователь с id %d не найден", userNotFoundId);
        when(userRepository.findById(userNotFoundId)).thenThrow(new NotFoundException(error));
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> requestService.add(userNotFoundId, ItemRequestNewDto.builder().description("description").build())
        );
        assertEquals(error, exception.getMessage());
        verify(requestRepository, times(0)).save(any());
    }

    @Test
    void findAllByUserId() {
        long userId = requester.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(requestRepository.findByRequesterId(userId)).thenReturn(List.of(request));
        List<ItemRequestDto> requests = requestService.findAllByUserId(userId);
        assertNotNull(requests);
        assertEquals(1, requests.size());
        verify(requestRepository, times(1)).findByRequesterId(userId);
    }

    @Test
    void findAll() {
        //Empty List
        long userId = requester.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, SORT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(itemRepository.findByRequestIdIn(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(requestRepository.findByRequesterIdNot(userId, page)).thenReturn(Page.empty());
        List<ItemRequestDto> requestDto = requestService.findAll(userId, from, size);
        assertNotNull(requestDto);
        assertEquals(0, requestDto.size());

        //Single List
        userId = owner.getId();
        long requestId = request.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));
        when(requestRepository.findByRequesterIdNot(userId, page)).thenReturn(new PageImpl<>(List.of(request)));
        requestDto = requestService.findAll(userId, from, size);
        assertNotNull(requestDto);
        assertEquals(1, requestDto.size());
    }

    @Test
    void findById() {
        long userId = requester.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        long requestId = request.getId();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));
        ItemRequestDto requestDto = requestService.findById(userId, requestId);

        assertNotNull(requestDto);
        assertEquals(requestId, requestDto.getId());
        assertEquals(1, requestDto.getItems().size());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());

        InOrder inOrder = inOrder(userRepository, requestRepository, itemRepository);
        inOrder.verify(userRepository).findById(userId);
        inOrder.verify(requestRepository).findById(requestId);
        inOrder.verify(itemRepository).findByRequestId(requestId);
    }
}
