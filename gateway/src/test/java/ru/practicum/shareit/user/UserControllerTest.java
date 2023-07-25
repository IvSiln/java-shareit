package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final String URL = "/users";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldValidateAdd() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("")
                .email("e@mail.ru")
                .build();
        String json = mapper.writeValueAsString(userDto);
        String error = "Имя не может быть пустым";
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        userDto.setName("name");
        userDto.setEmail("");
        json = mapper.writeValueAsString(userDto);
        error = "E-mail не может быть пустым";
        this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));
    }

    @Test
    void shouldValidatePatch() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("")
                .build();
        String json = mapper.writeValueAsString(userDto);
        String error = "Имя не может быть пустым";
        mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        //fail empty email
        userDto.setName(null);
        userDto.setEmail("");
        json = mapper.writeValueAsString(userDto);
        error = "E-mail не может быть пустым";
        this.mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error)));

        userDto.setEmail("patched");
        json = mapper.writeValueAsString(userDto);
        error = "Введен некорректный e-mail";
        mockMvc.perform(patch(URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email", containsString(error)));
    }
}