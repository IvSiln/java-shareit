package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");
    private static final Instant NOW = Instant.now();
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;


    @BeforeEach
    void setup() {
        Instant start = NOW.minusSeconds(120);
        Instant end = NOW.minusSeconds(60);
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner.setId(1L);

        booker = new User();
        booker.setName("name2");
        booker.setEmail("e2@mail.ru");
        booker.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setName("Ждун");
        item.setDescription("Бздун и говорун, ожидает карачун");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        comment = new Comment();
        comment.setText("text");
        comment.setAuthor(booker);
        comment.setItem(item);
        comment.setCreated(NOW);
        comment.setId(1L);
    }

    @Test
    void findAllByUserId() {
        long userId = booker.getId();
        int from = 0;
        int size = 1;
        PageRequest page = PageRequest.of(from / size, size);
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findByOwnerId(userId, page)).thenReturn(Page.empty());
        List<ItemBookingCommentsDto> itemDtos = itemService.findAllByUserId(userId, from, size);
        assertNotNull(itemDtos);
        assertEquals(0, itemDtos.size());

        userId = owner.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(commentRepository.findAllByItemIdIn(List.of(item.getId()), SORT))
                .thenReturn(List.of(comment));
        when(bookingRepository.findByItemIdInAndStatusOrStatusOrderByStartAsc(List.of(item.getId()),
                Status.APPROVED, Status.WAITING)).thenReturn(List.of(booking));
        when(itemRepository.findByOwnerId(userId, page)).thenReturn(new PageImpl<>(List.of(item)));
        itemDtos = itemService.findAllByUserId(userId, from, size);
        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(booking.getId(), itemDtos.get(0).getLastBooking().getId());
    }

    @Test
    void findById() {
        long ownerId = owner.getId();
        long itemId = item.getId();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatusOrStatusOrderByStartAsc(itemId,
                Status.APPROVED, Status.WAITING)).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemId(itemId, SORT))
                .thenReturn(List.of(comment));
        ItemBookingCommentsDto itemDto = itemService.findById(ownerId, itemId);
        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        assertEquals(comment.getId(), itemDto.getComments().get(0).getId());
    }

    @Test
    void findByText() {
        int from = 0;
        int size = 1;

        String text = "";
        List<ItemDto> itemDtos = itemService.findByText(text, from, size);
        assertNotNull(itemDtos);
        assertEquals(0, itemDtos.size());

        text = "Ждун";
        when(itemRepository.searchWithPaging(any(), any())).thenReturn(new PageImpl<>(List.of(item)));
        itemDtos = itemService.findByText(text, from, size);
        assertNotNull(itemDtos);
        assertEquals(1, itemDtos.size());
        assertEquals(item.getId(), itemDtos.get(0).getId());
    }

    @Test
    void add() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDtoToSave = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
        ItemDto itemDto = itemService.add(userId, itemDtoToSave);
        assertNotNull(itemDto);
        assertEquals(itemId, itemDto.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void patch() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        String parameterName = "Название";
        String error = String.format("%s не может быть пустым", parameterName);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.patch(userId, itemId, ItemDto.builder().name("").build())
        );
        assertEquals(error, exception.getMessage());

        parameterName = "Описание";
        error = String.format("%s не может быть пустым", parameterName);
        exception = assertThrows(
                ValidationException.class,
                () -> itemService.patch(userId, itemId, ItemDto.builder().description("").build())
        );
        assertEquals(error, exception.getMessage());

        String newName = "nameUpdate";
        String newDescription = "newDescription";
        item.setName(newName);
        item.setDescription(newDescription);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDtoToUpdate = ItemDto.builder()
                .name(newName)
                .description(newDescription)
                .build();
        ItemDto itemDto = itemService.patch(userId, itemId, itemDtoToUpdate);
        assertNotNull(itemDto);
        assertEquals("nameUpdate", itemDto.getName());
    }

    @Test
    void delete() {
        long userId = owner.getId();
        long itemId = item.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(itemId);
        itemService.delete(userId, itemId);

        verify(userRepository, times(1)).findById(any());
        verify(itemRepository, times(1)).deleteById(any());

        //Fail by Not Owner
        long notOwnerId = booker.getId();
        String error = String.format("Пользователь с id %d не владеет вещью с id %d", notOwnerId, itemId);
        when(userRepository.findById(notOwnerId)).thenReturn(Optional.of(booker));
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.delete(notOwnerId, itemId));
        assertEquals(error, exception.getMessage());
    }

    @Test
    void addComment() {
        long userId = booker.getId();
        long itemId = item.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto commentDto = itemService.addComment(userId, itemId, CommentDto.builder().text("text").build());
        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());

        verify(commentRepository, times(1)).save(any());

        long ownerId = owner.getId();
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());
        String error = String.format("Пользователь с id %s не арендовал вещь с id %s", ownerId, itemId);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(ownerId, itemId, CommentDto.builder().text("text").build())
        );
        assertEquals(error, exception.getMessage());
    }
}