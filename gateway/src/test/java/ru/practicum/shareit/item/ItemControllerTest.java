package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String URL = "/items";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldValidateAdd() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("description")
                .available(true)
                .build();
        String json = mapper.writeValueAsString(itemDto);
        String error = "Название не может быть пустым";
        mockMvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));


        itemDto = ItemDto.builder().name("name").description("").build();
        json = mapper.writeValueAsString(itemDto);
        error = "Описание не может быть пустым";
        mockMvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

    }

    @Test
    void shouldValidatePatch() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .build();
        String json = mapper.writeValueAsString(itemDto);
        String error = "Название не может быть пустым";
        mockMvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        itemDto = ItemDto.builder().name("name").description("").build();
        json = mapper.writeValueAsString(itemDto);
        error = "Описание не может быть пустым";
        mockMvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void shouldValidateAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder().build();
        String jsonIn = mapper.writeValueAsString(commentDto);
        String error = "Текст комментария не может быть пустым";
        mockMvc.perform(post(URL + "/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonIn))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.text", containsString(error)));
    }
}