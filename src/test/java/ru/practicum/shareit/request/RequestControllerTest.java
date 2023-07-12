package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.controller.RequestController;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;
import ru.practicum.shareit.requests.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    private static final String URL = "/requests";
    private static final String HEADER = "X-Sharer-User-Id";

    private static final int SIZE_DEFAULT = 100;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestDto.ItemRequestDtoBuilder builder;

    @BeforeEach
    void setupBuilder() {
        builder = ItemRequestDto.builder()
                .id(1L)
                .description("Help, I need some body")
                .created(LocalDateTime.now());

    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldAddRequest() throws Exception {
        ItemRequestNewDto requestIn = ItemRequestNewDto.builder()
                .build();
        String json = mapper.writeValueAsString(requestIn);
        String error = "must not be blank";
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.description", containsString(error)));

        requestIn.setDescription("Help, I need some body");
        ItemRequestNewDto requestOut = ItemRequestNewDto.builder()
                .id(1L)
                .description("Help, I need some body")
                .created(LocalDateTime.now())
                .build();
        json = mapper.writeValueAsString(requestIn);
        when(requestService.add(1L, requestIn)).thenReturn(requestOut);
        mockMvc.perform(post(URL)
                        .header(HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestOut.getDescription()), String.class));

        error = String.format("Пользователь с id %d не найден", -1);
        when(requestService.add(-1L, requestIn)).thenThrow(new NotFoundException(error));
        mockMvc.perform(post(URL)
                        .header(HEADER, -1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void shouldFindAllByUserId() throws Exception {
        when(requestService.findAllByUserId(2L)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(URL)
                        .header(HEADER, 2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        //Single List
        itemRequestDto = builder.build();
        when(requestService.findAllByUserId(1L)).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get(URL)
                        .header(HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        String error = String.format("Пользователь с id %d не найден", -1);
        when(requestService.findAllByUserId(-1L)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL)
                        .header(HEADER, -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void shouldFindAll() throws Exception {
        when(requestService.findAll(1L, 0, SIZE_DEFAULT)).thenReturn(new ArrayList<>());
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, 1)
                        .param("from", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        itemRequestDto = builder.build();
        when(requestService.findAll(1L, 0, 1)).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription()), String.class));

        String error = String.format("Пользователь с id %d не найден", -1);
        when(requestService.findAll(-1L, 0, 1)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, -1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));

        error = "findAll.from: must be greater than or equal to 0";
        mockMvc.perform(get(URL + "/all")
                        .header(HEADER, 1)
                        .param("from", "-1")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void shouldFindById() throws Exception {
        itemRequestDto = builder.build();
        when(requestService.findById(1L, 1L)).thenReturn(itemRequestDto);
        mockMvc.perform(get(URL + "/1")
                        .header(HEADER, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        String error = String.format("Пользователь с id %d не найден", -1);
        when(requestService.findById(-1L, 1L)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL + "/1")
                        .header(HEADER, -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));

        error = String.format("Запрос с id %d не найден", 42);
        when(requestService.findById(1L, 42L)).thenThrow(new NotFoundException(error));
        mockMvc.perform(get(URL + "/42")
                        .header(HEADER, 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }
}
