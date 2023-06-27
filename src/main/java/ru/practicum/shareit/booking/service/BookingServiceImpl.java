package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerBookingException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    public static final String END_DATE_AFTER_START_DATE_ERROR_MESSAGE = "The end date of the booking must be after the start date";
    public static final String USER_IS_NOT_OWNER_MESSAGE = "A user with id {} does not own a thing with id {}";
    private static final String USER_NOT_FOUND_MESSAGE = "User with id %d not found";
    private static final String BOOKING_NOT_FOUND_MESSAGE = "Booking with id %d not found";
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item with id %d not found";
    private static final String USER_IS_OWNER_MESSAGE = "The user with id %d is the owner of the item with id %d";
    private static final String ITEM_NOT_AVAILABLE_MESSAGE = "Item with id %d is not available for booking";
    private static final String BOOKING_ALREADY_APPROVED_MESSAGE = "Booking with id %d has already been confirmed";
    private static final String BOOKING_ALREADY_REJECTED_MESSAGE = "Booking with id %d has already been rejected";
    private static final String UNKNOWN_STATUS_MESSAGE = "Unknown state: UNSUPPORTED_STATUS";
    private static final String USER_CANNOT_VIEW_BOOKING = "A user with id {} cannot view a booking with id {}";
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingOutDto findById(Long userId, long bookingId) {
        checkUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_MESSAGE, bookingId)));
        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();
        if (userId != bookerId && userId != ownerId) {
            log.warn(USER_CANNOT_VIEW_BOOKING, userId, bookingId);
            throw new NotFoundException(
                    String.format(USER_CANNOT_VIEW_BOOKING, userId, bookingId));
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingOutDto> findByState(Long userId, State state) {
        checkUser(userId);
        List<Booking> bookings = new ArrayList<>();
        Instant now = Instant.now();
        switch (state) {
            case ALL:
                bookings = repository.findByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = repository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = repository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now);
                break;
            case CURRENT:
                bookings = repository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now);
                break;
            case WAITING:
                bookings = repository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case UNKNOWN:
                log.warn(UNKNOWN_STATUS_MESSAGE);
                throw new UnsupportedStatusException(UNKNOWN_STATUS_MESSAGE);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Override
    public List<BookingOutDto> findByOwnerItemsAndState(Long userId, State state) {
        checkUser(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings;
        Instant now = Instant.now();

        switch (state) {
            case ALL:
                bookings = repository.findByItemIdInOrderByStartDesc(itemIds);
                break;
            case PAST:
                bookings = repository.findByItemIdInAndEndIsBeforeOrderByStartDesc(itemIds, now);
                break;
            case FUTURE:
                bookings = repository.findByItemIdInAndStartIsAfterOrderByStartDesc(itemIds, now);
                break;
            case CURRENT:
                bookings = repository.findByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(itemIds, now, now);
                break;
            case WAITING:
                bookings = repository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.findByItemIdInAndStatusOrderByStartDesc(itemIds, Status.REJECTED);
                break;
            default:
                log.warn(UNKNOWN_STATUS_MESSAGE);
                throw new UnsupportedStatusException(UNKNOWN_STATUS_MESSAGE);
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public BookingOutDto add(Long userId, BookingInDto bookingDto) {
        User booker = checkUser(userId);
        long itemId = bookingDto.getItemId();
        Item item = checkItem(itemId);
        if (isOwner(userId, itemId)) {
            log.warn(USER_IS_OWNER_MESSAGE, userId, item.getId());
            throw new OwnerBookingException(String.format(
                    USER_IS_OWNER_MESSAGE, userId, item.getId()));
        }
        if (!item.isAvailable()) {
            log.warn(ITEM_NOT_AVAILABLE_MESSAGE, item.getId());
            throw new ValidationException(String.format(
                    ITEM_NOT_AVAILABLE_MESSAGE, item.getId()));
        }
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            log.warn(END_DATE_AFTER_START_DATE_ERROR_MESSAGE);
            throw new ValidationException(END_DATE_AFTER_START_DATE_ERROR_MESSAGE);
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        booking = repository.save(booking);
        booking.setItem(item);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Transactional
    @Override
    public BookingOutDto patch(Long userId, long bookingId, boolean approved) {
        checkUser(userId);
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_MESSAGE, bookingId)));
        long itemId = booking.getItem().getId();
        if (!isOwner(userId, itemId)) {
            log.warn(USER_IS_NOT_OWNER_MESSAGE, userId, itemId);
            throw new NotFoundException(String.format(USER_IS_OWNER_MESSAGE, userId, itemId));
        }
        Status status;
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                log.warn(BOOKING_ALREADY_APPROVED_MESSAGE, bookingId);
                throw new ValidationException(String.format(BOOKING_ALREADY_APPROVED_MESSAGE, bookingId));
            }
            status = Status.APPROVED;
        } else {
            if (booking.getStatus().equals(Status.REJECTED)) {
                log.warn(BOOKING_ALREADY_REJECTED_MESSAGE, bookingId);
                throw new ValidationException(String.format(BOOKING_ALREADY_REJECTED_MESSAGE, bookingId));
            }
            status = Status.REJECTED;
        }
        booking.setStatus(status);
        booking = repository.save(booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId)));
    }

    private Item checkItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, itemId)));
    }

    private boolean isOwner(long userId, long itemId) {
        return itemRepository.findByOwnerId(userId).stream().anyMatch(it -> it.getId() == itemId);
    }
}