package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingOutDtoTest {

    @Test
    void testBuilderAndGetters() {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
        ItemDto itemDto = ItemDto.builder().id(1L).name("Test Item").build();
        UserDto userDto = UserDto.builder().id(2L).name("Test User").build();
        Status status = Status.APPROVED;

        BookingOutDto dto = BookingOutDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(status)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(start);
        assertThat(dto.getEnd()).isEqualTo(end);
        assertThat(dto.getItem()).isEqualTo(itemDto);
        assertThat(dto.getBooker()).isEqualTo(userDto);
        assertThat(dto.getStatus()).isEqualTo(status);
    }

    @Test
    void testJsonSerialization() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 1, 2, 0, 0);
        ItemDto itemDto = ItemDto.builder().id(1L).name("Test Item").build();
        UserDto userDto = UserDto.builder().id(2L).name("Test User").build();
        Status status = Status.APPROVED;

        BookingOutDto dto = BookingOutDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(itemDto)
                .booker(userDto)
                .status(status)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);
        BookingOutDto deserializedDto = objectMapper.readValue(json, BookingOutDto.class);

        assertThat(deserializedDto).isEqualTo(dto);
    }
}
