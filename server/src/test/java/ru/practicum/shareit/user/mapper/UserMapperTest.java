package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    void testToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testToUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("johndoe@example.com")
                .build();

        User user = UserMapper.toUser(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}
