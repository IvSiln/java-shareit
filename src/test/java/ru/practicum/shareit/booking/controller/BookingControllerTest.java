package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String URL = "/bookings";
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private BookingOutDto bookingOutDto;

    private BookingInDto.BookingInDtoBuilder builderIn;
    private BookingOutDto.BookingOutDtoBuilder builderOut;

    @BeforeEach
    void setupBuilder() {
        LocalDateTime now = LocalDateTime.now();
        UserDto.UserDtoBuilder userDtoBuilder = UserDto.builder()
                .id(1L)
                .name("name")
                .email("e@mail.ru");
        ItemDto.ItemDtoBuilder itemDtoBuilder = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true);
        builderIn = BookingInDto.builder()
                .itemId(1L)
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2));
        builderOut = BookingOutDto.builder()
                .id(1L)
                .booker(userDtoBuilder.build())
                .item(itemDtoBuilder.build())
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .status(Status.WAITING);
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldFindById() throws Exception {
        bookingOutDto = builderOut.build();
        when(bookingService.findById(1L, 1L)).thenReturn(bookingOutDto);
        mockMvc.perform(get(URL + "/1")
                        .header(HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", containsString(String.valueOf(
                        bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));

        String error = String.format("Пользователь с id %d не найден", -1);
        when(bookingService.findById(-1L, 1L)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL + "/1")
                        .header(HEADER, -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));

        error = String.format("Бронирование с id %d не найдено", 42);
        when(bookingService.findById(1L, 42L)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL + "/42")
                        .header(HEADER, 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));
    }

    @Test
    void shouldAdd() throws Exception {
        BookingInDto bookingInDto = builderIn.build();
        bookingOutDto = builderOut.build();
        String json = mapper.writeValueAsString(bookingInDto);
        when(bookingService.add(1L, bookingInDto)).thenReturn(bookingOutDto);
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));

        //fail by userId
        String error = String.format("Пользователь с id %d не найден", -1);
        when(bookingService.add(-1L, bookingInDto)).thenThrow(new NotFoundException(error));
        this.mockMvc
                .perform(post(URL)
                        .header(HEADER, -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));

        error = String.format("Вещь с id %d не найдена", 42);
        bookingInDto.setItemId(42L);
        json = mapper.writeValueAsString(bookingInDto);
        when(bookingService.add(1L, bookingInDto)).thenThrow(new NotFoundException(error));
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));

        bookingInDto.setItemId(null);
        json = mapper.writeValueAsString(bookingInDto);
        error = "add.bookingDto.itemId: Item not specified";
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()))
                .andExpect(jsonPath("$.error", containsString(error), String.class));

        bookingInDto.setItemId(1L);
        bookingInDto.setStart(null);
        json = mapper.writeValueAsString(bookingInDto);
        error = "add.bookingDto.start: The booking start date cannot be empty";
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()))
                .andExpect(jsonPath("$.error", containsString(error), String.class));

        //fail by end time
        bookingInDto.setStart(LocalDateTime.now().plusMinutes(1));
        bookingInDto.setEnd(null);
        json = mapper.writeValueAsString(bookingInDto);
        error = "add.bookingDto.end: The end date of the booking cannot be empty";
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void shouldPatch() throws Exception {
        bookingOutDto = builderOut.status(Status.APPROVED).build();
        when(bookingService.patch(1L, 1L, true)).thenReturn(bookingOutDto);
        mockMvc.perform(patch(URL + "/1")
                        .header(HEADER, 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        containsString(String.valueOf(bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));

        bookingOutDto = builderOut.status(Status.REJECTED).build();
        when(bookingService.patch(1L, 1L, false)).thenReturn(bookingOutDto);
        mockMvc.perform(patch(URL + "/1")
                        .header(HEADER, 1)
                        .param("approved", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", containsString(String.valueOf(
                        bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));

        String error = String.format("Бронирование с id %d уже отклонено", 1);
        when(bookingService.patch(1L, 1L, false)).thenThrow(new BadRequestException(error));
        mockMvc.perform(patch(URL + "/1")
                        .header(HEADER, 1)
                        .param("approved", "false"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void shouldFindByState() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto booking1 = builderOut.id(1L).start(now.minusDays(1)).end(now).status(Status.APPROVED).build();
        BookingOutDto booking2 = builderOut.id(2L).start(now.plusDays(1)).end(now.plusDays(2)).status(Status.WAITING).build();
        BookingOutDto booking3 = builderOut.id(3L).start(now.plusDays(2)).end(now.plusDays(3)).status(Status.REJECTED).build();

        when(bookingService.findByState(1L, State.CURRENT, 0, 10)).thenReturn(List.of(booking1));
        mockMvc.perform(get(URL)
                        .header(HEADER, 1)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        containsString(String.valueOf(booking1.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[0].status", is(booking1.getStatus().toString()), String.class));

        when(bookingService.findByState(1L, State.PAST, 0, 10)).thenReturn(List.of());
        mockMvc.perform(get(URL)
                        .header(HEADER, 1)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        when(bookingService.findByState(1L, State.FUTURE, 0, 10)).thenReturn(List.of(booking2, booking3));
        mockMvc.perform(get(URL)
                        .header(HEADER, 1)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        containsString(String.valueOf(booking2.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[0].status", is(booking2.getStatus().toString()), String.class))
                .andExpect(jsonPath("$[1].id", is(booking3.getId()), Long.class))
                .andExpect(jsonPath("$[1].start",
                        containsString(String.valueOf(booking3.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[1].status", is(booking3.getStatus().toString()), String.class));
    }

    @Test
    void shouldFindByOwnerItemsAndState() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto booking1 = builderOut.id(1L).start(now.minusDays(1)).end(now).status(Status.APPROVED).build();
        BookingOutDto booking2 = builderOut.id(2L).start(now.plusDays(1)).end(now.plusDays(2)).status(Status.WAITING).build();

        when(bookingService.findByOwnerItemsAndState(1L, State.CURRENT, 0, 10)).thenReturn(List.of(booking1));
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, 1)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        containsString(String.valueOf(booking1.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[0].status", is(booking1.getStatus().toString()), String.class));

        when(bookingService.findByOwnerItemsAndState(1L, State.PAST, 0, 10)).thenReturn(List.of());
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, 1)
                        .param("state", "PAST")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        when(bookingService.findByOwnerItemsAndState(1L, State.FUTURE, 0, 10)).thenReturn(List.of(booking2));
        mockMvc.perform(get(URL + "/owner")
                        .header(HEADER, 1)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking2.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        containsString(String.valueOf(booking2.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[0].status", is(booking2.getStatus().toString()), String.class));
    }

}