package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.SortUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    private static final Instant NOW = Instant.now();
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private final Sort SORT = SortUtil.DESCENDING_SORT_BY_START;
    @Mock
    BookingRepository repository;
    @InjectMocks
    BookingServiceImpl service;
    @Mock
    private UserRepository userRepo;
    @Mock
    private ItemRepository itemRepo;
    private User owner;
    private User booker;

    private User user;
    private Item item;
    private Booking booking;
    private Booking booking2;

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

        user = new User();
        user.setName("name3");
        user.setEmail("e3@mail.ru");
        user.setId(3L);

        item = new Item();
        item.setId(1L);
        item.setName("Мойка");
        item.setDescription("Даже брюки, даже брюки убежали от тебя");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.APPROVED);

        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(start.plusSeconds(10));
        booking2.setEnd(end.plusSeconds(10));
        booking2.setItem(item);
        booking2.setBooker(booker);
        booking2.setStatus(Status.APPROVED);
    }

    @Test
    void findById() {
        long userId = user.getId();
        long bookingId = booking.getId();
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        String error = String.format(
                "Пользователь с id %d не может просматривать бронирование с id %d", userId, bookingId);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.findById(userId, bookingId)
        );
        assertEquals(error, exception.getMessage());

        long ownerId = owner.getId();
        when(userRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingOutDto bookingOutDto = service.findById(ownerId, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());

        long bookerId = owner.getId();
        when(userRepo.findById(bookerId)).thenReturn(Optional.of(booker));
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        bookingOutDto = service.findById(bookerId, bookingId);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void findByState() {
        int from = 0;
        int size = 1;
        long userId = booker.getId();
        PageRequest page = PageRequest.of(0, size, SORT);
        when(userRepo.findById(userId)).thenReturn(Optional.of(booker));

        String error = "Unknown state: UNSUPPORTED_STATUS";
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.findByState(userId, State.UNSUPPORTED_STATUS, from, size)
        );
        assertEquals(error, exception.getMessage());

        when(repository.findByBookerId(userId, page)).thenReturn(new PageImpl<>(List.of(booking)));
        List<BookingOutDto> byState = service.findByState(userId, State.ALL, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());
        assertEquals(booking.getId(), byState.get(0).getId());

        when(repository.findByBookerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        byState = service.findByState(userId, State.PAST, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());

        booking.setEnd(NOW.plusSeconds(120));

        when(repository.findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        byState = service.findByState(userId, State.CURRENT, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());

        booking.setStart(NOW.plusSeconds(60));

        when(repository.findByBookerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        byState = service.findByState(userId, State.FUTURE, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());

        booking.setStatus(Status.WAITING);

        when(repository.findByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        byState = service.findByState(userId, State.WAITING, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());

        booking.setStatus(Status.REJECTED);

        byState = service.findByState(userId, State.REJECTED, from, size);

        assertNotNull(byState);
        assertEquals(1, byState.size());
    }

    @Test
    void findByOwnerItemsAndState() {
        int from = 0;
        int size = 1;
        long userId = owner.getId();
        PageRequest page = PageRequest.of(0, size, SORT);
        when(userRepo.findById(userId)).thenReturn(Optional.of(owner));

        String error = "Unknown state: UNSUPPORTED_STATUS";
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.findByOwnerItemsAndState(userId, State.UNSUPPORTED_STATUS, from, size)
        );
        assertEquals(error, exception.getMessage());

        when(repository.findByItemOwnerId(userId, page)).thenReturn(new PageImpl<>(List.of(booking)));

        List<BookingOutDto> bookingOutDto = service.findByOwnerItemsAndState(userId, State.ALL, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());
        assertEquals(booking.getId(), bookingOutDto.get(0).getId());

        when(repository.findByItemOwnerIdAndEndIsBefore(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        bookingOutDto = service.findByOwnerItemsAndState(userId, State.PAST, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());

        booking.setEnd(NOW.plusSeconds(120));
        when(repository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        bookingOutDto = service.findByOwnerItemsAndState(userId, State.CURRENT, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());

        booking.setStart(NOW.plusSeconds(60));
        when(repository.findByItemOwnerIdAndStartIsAfter(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(booking)));

        bookingOutDto = service.findByOwnerItemsAndState(userId, State.FUTURE, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());

        booking.setStatus(Status.WAITING);
        when(repository.findByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        bookingOutDto = service.findByOwnerItemsAndState(userId, State.WAITING, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());

        booking.setStatus(Status.REJECTED);
        when(repository.findByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(new PageImpl<>(List.of(booking)));

        bookingOutDto = service.findByOwnerItemsAndState(userId, State.REJECTED, from, size);

        assertNotNull(bookingOutDto);
        assertEquals(1, bookingOutDto.size());
    }

    @Test
    void add() {
        long ownerId = owner.getId();
        long itemId = item.getId();
        LocalDateTime start = LocalDateTime.ofInstant(booking.getStart(), ZONE_ID);
        LocalDateTime end = LocalDateTime.ofInstant(booking.getEnd(), ZONE_ID);
        BookingInDto bookingToSave = BookingInDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        when(userRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        String error = String.format("Пользователь с id %d владелец вещи с id %d", ownerId, itemId);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> service.add(ownerId, bookingToSave));
        assertEquals(error, exception.getMessage());

        item.setAvailable(false);
        long bookerId = booker.getId();
        when(userRepo.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        error = String.format("Вещь с id %d  недоступна для бронирования", itemId);
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.add(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());

        item.setAvailable(true);
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(item));
        when(repository.findBookingsAtSameTime(itemId, Status.APPROVED, booking.getStart(), booking.getEnd()))
                .thenReturn(List.of(booking2));
        error = "Время для аренды недоступно";
        ex = assertThrows(
                BadRequestException.class,
                () -> service.add(bookerId, bookingToSave));
        assertEquals(error, ex.getMessage());

        when(repository.findBookingsAtSameTime(itemId, Status.APPROVED, booking.getStart(), booking.getEnd()))
                .thenReturn(Collections.emptyList());
        when(repository.save(any())).thenReturn(booking);
        BookingOutDto bookingOutDto = service.add(bookerId, bookingToSave);

        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }

    @Test
    void patch() {
        long userId = owner.getId();
        long bookingId = booking.getId();

        when(userRepo.findById(userId)).thenReturn(Optional.of(owner));
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        String error = String.format("Бронирование с id %d уже подтверждено", bookingId);
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> service.patch(userId, bookingId, true)
        );
        assertEquals(error, exception.getMessage());

        when(repository.save(any())).thenReturn(booking);

        BookingOutDto bookingOutDto = service.patch(userId, bookingId, false);
        assertNotNull(bookingOutDto);
        assertEquals(booking.getId(), bookingOutDto.getId());
    }
}
