package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutDtoTest {
    @Autowired
    private JacksonTester<BookingOutDto> json;

    private static final LocalDateTime START = LocalDateTime.of(2023, 1, 1, 0, 0);
    private static final LocalDateTime END = LocalDateTime.of(2023, 1, 2, 0, 0);

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    void testBuilderAndGetters() {
        ItemDto itemDto = ItemDto.builder().id(1L).name("Test Item").build();
        UserDto userDto = UserDto.builder().id(2L).name("Test User").build();
        Status status = Status.APPROVED;

        BookingOutDto dto = BookingOutDto.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(itemDto)
                .booker(userDto)
                .status(status)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(START);
        assertThat(dto.getEnd()).isEqualTo(END);
        assertThat(dto.getItem()).isEqualTo(itemDto);
        assertThat(dto.getBooker()).isEqualTo(userDto);
        assertThat(dto.getStatus()).isEqualTo(status);
    }

    @Test
    void testJsonSerialization() throws Exception {
        ItemDto itemDto = ItemDto.builder().id(1L).name("Test Item").build();
        UserDto userDto = UserDto.builder().id(2L).name("Test User").build();
        Status status = Status.APPROVED;

        BookingOutDto dto = BookingOutDto.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(itemDto)
                .booker(userDto)
                .status(status)
                .build();

        String jsonString = json.write(dto).getJson();
        BookingOutDto deserializedDto = json.parse(jsonString).getObject();

        assertThat(deserializedDto).isEqualTo(dto);
    }
}
