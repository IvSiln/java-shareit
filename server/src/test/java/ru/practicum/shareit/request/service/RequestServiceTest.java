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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @Mock
    private RequestRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepo;

    @InjectMocks
    private RequestServiceImpl service;

    private UserDto requester;
    private UserDto owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setName("name");
        user.setEmail("e@mail.ru");
        user.setId(1L);
        owner = UserMapper.toUserDto(user);
        user.setName("name2");
        user.setEmail("e2@mail.ru");
        user.setId(2L);
        requester = UserMapper.toUserDto(user);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequester(UserMapper.toUser(requester));
        request.setCreated(Instant.now());
        request.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(UserMapper.toUser(owner));
        item.setRequest(request);
    }

    @Test
    void add() {
        // Regular case
        when(repository.save(any())).thenReturn(request);
        when(userService.findById(requester.getId())).thenReturn(requester);

        ItemRequestNewDto requestDto = service.add(requester.getId(),
                ItemRequestNewDto.builder().description("description").build());

        assertNotNull(requestDto);
        assertEquals(request.getId(), requestDto.getId());
        verify(repository, times(1)).save(any());
    }

    @Test
    void addFailByUserNotFound() {
        // Fail By User Not Found
        long userNotFoundId = 0L;
        String error = String.format("Пользователь с id %d не найден", userNotFoundId);
        when(userService.findById(userNotFoundId)).thenThrow(new NotFoundException(error));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.add(userNotFoundId, ItemRequestNewDto.builder().description("description").build())
        );

        assertEquals(error, exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void findAllByUserId() {
        long userId = requester.getId();
        when(userService.findById(userId)).thenReturn(requester);
        when(repository.findByRequesterId(userId)).thenReturn(List.of(request));

        List<ItemRequestDto> requestDtos = service.findAllByUserId(userId);

        assertNotNull(requestDtos);
        assertEquals(1, requestDtos.size());
        verify(repository, times(1)).findByRequesterId(userId);
    }

    @Test
    void findAll() {
        // Empty List
        long userId = requester.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size, SORT);
        when(userService.findById(userId)).thenReturn(requester);
        when(itemRepo.findByRequestIdIn(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(repository.findByRequesterIdNot(userId, page)).thenReturn(Page.empty());

        List<ItemRequestDto> requestDtos = service.findAll(userId, from, size);

        assertNotNull(requestDtos);
        assertEquals(0, requestDtos.size());

        // Single List
        userId = owner.getId();
        long requestId = request.getId();
        when(userService.findById(userId)).thenReturn(owner);
        when(itemRepo.findByRequestIdIn(List.of(requestId))).thenReturn(List.of(item));
        when(repository.findByRequesterIdNot(userId, page)).thenReturn(new PageImpl<>(List.of(request)));

        requestDtos = service.findAll(userId, from, size);

        assertNotNull(requestDtos);
        assertEquals(1, requestDtos.size());
    }

    @Test
    void findById() {
        long userId = requester.getId();
        when(userService.findById(userId)).thenReturn(requester);
        long requestId = request.getId();
        when(repository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepo.findByRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestDto requestDto = service.findById(userId, requestId);

        assertNotNull(requestDto);
        assertEquals(requestId, requestDto.getId());
        assertEquals(1, requestDto.getItems().size());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());

        InOrder inOrder = inOrder(userService, repository, itemRepo);
        inOrder.verify(userService).findById(userId);
        inOrder.verify(repository).findById(requestId);
        inOrder.verify(itemRepo).findByRequestId(requestId);
    }
}
