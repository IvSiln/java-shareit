package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Override
    public UserDto get(Long id) {
        User user = userStorage.get(id);
        return user != null ? userMapper.toUserDto(user) : null;
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = userStorage.add(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto patch(UserDto userDto, Long id) {
        User user = userMapper.toUser(userDto);
        user.setId(id);
        User updatedUser = userStorage.patch(user);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public boolean delete(Long id) {
        return userStorage.delete(id);
    }
}
