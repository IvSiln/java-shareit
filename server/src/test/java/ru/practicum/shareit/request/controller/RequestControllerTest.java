package ru.practicum.shareit.request.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.service.RequestService;

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

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

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
        requestIn.setDescription("Нужен мужчина с перфоратором");
        ItemRequestNewDto requestOut = ItemRequestNewDto.builder()
                .id(1L)
                .description("Нужен мужчина с перфоратором")
                .created(LocalDateTime.now())
                .build();
        String json = mapper.writeValueAsString(requestIn);
        when(requestService.add(1L, requestIn)).thenReturn(requestOut);
        mockMvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestOut.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestOut.getDescription()), String.class));

        // Wrong user
        String error = String.format("Пользователь с id %d не найден", -1);
        when(requestService.add(-1L, requestIn)).thenThrow(new NotFoundException(error));
        mockMvc.perform(post(URL)
                        .header("X-Sharer-User-Id", -1)
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
        ItemRequestDto itemRequestDto = builder.build();
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
}
