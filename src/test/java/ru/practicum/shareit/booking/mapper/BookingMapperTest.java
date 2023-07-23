package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {
    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    @Test
    void testToBookingDtoOut() {
        // Создаем объект Booking для преобразования в BookingOutDto
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.now());
        booking.setEnd(Instant.now().plusSeconds(3600));

        Item item = new Item();
        item.setId(2L);

        User user = new User();
        user.setId(3L);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        // Выполняем преобразование
        BookingOutDto bookingOutDto = BookingMapper.toBookingDtoOut(booking);

        // Проверяем, что значения полей соответствуют ожидаемым
        assertEquals(booking.getId(), bookingOutDto.getId());
        assertEquals(LocalDateTime.ofInstant(booking.getStart(), ZoneId.systemDefault()), bookingOutDto.getStart());
        assertEquals(LocalDateTime.ofInstant(booking.getEnd(), ZoneId.systemDefault()), bookingOutDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), bookingOutDto.getItem());
        assertEquals(UserMapper.toUserDto(booking.getBooker()), bookingOutDto.getBooker());
        assertEquals(booking.getStatus(), bookingOutDto.getStatus());
    }

    @Test
    void testToBooking() {
        // Создаем объект BookingInDto с использованием билдера
        BookingInDto bookingDto = BookingInDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(3600))
                .itemId(2L)
                .status(Status.APPROVED)
                .build();

        // Выполняем преобразование
        Booking booking = BookingMapper.toBooking(bookingDto);

        // Проверяем, что значения полей соответствуют ожидаемым
        assertEquals(bookingDto.getId(), booking.getId());
        assertEquals(bookingDto.getStart().toInstant(ZONE_OFFSET), booking.getStart());
        assertEquals(bookingDto.getEnd().toInstant(ZONE_OFFSET), booking.getEnd());

    }

    @Test
    void testToBookingForItemsOutDto() {
        // Создаем объект Booking для преобразования в BookingResponseDto
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(Instant.now());
        booking.setEnd(Instant.now().plusSeconds(3600));

        User user = new User();
        user.setId(2L);

        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        // Выполняем преобразование
        BookingResponseDto bookingResponseDto = BookingMapper.toBookingForItemsOutDto(booking);

        // Проверяем, что значения полей соответствуют ожидаемым
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(LocalDateTime.ofInstant(booking.getStart(), ZoneId.systemDefault()), bookingResponseDto.getStart());
        assertEquals(LocalDateTime.ofInstant(booking.getEnd(), ZoneId.systemDefault()), bookingResponseDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingResponseDto.getBookerId());
        assertEquals(booking.getStatus(), bookingResponseDto.getStatus());
    }
}
