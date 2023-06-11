package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserStorage userStorage;

    @Override
    public Optional<UserDto> get(Long id) {
        return userStorage.get(id)
                .map(userMapper::toUserDto);
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
        String email = userDto.getEmail();
        if (userStorage.findUserByEmail(email).isPresent()) {
            throw new EmailException("User with email " + email + " already exists");
        }
        User user = userMapper.toUser(userDto);
        user.setEmail(email);
        User savedUser = userStorage.add(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto patch(UserDto userDto, Long id) {
        User user = userMapper.toUser(userDto);
        user.setId(id);
        validateEmail(user);
        User updatedUser = userStorage.patch(user);
        return userMapper.toUserDto(updatedUser);
    }


    @Override
    public boolean delete(Long id) {
        return userStorage.delete(id);
    }

    private void validateEmail(User user) {
        if (isEmailAlreadyExists(user)) {
            throw new EmailException("A user with this email address " +
                    user.getEmail() + "already exists!");
        }
    }

    public boolean isEmailAlreadyExists(User user) {
        return userStorage.getAll()
                .stream()
                .anyMatch(stored -> stored.getEmail().equalsIgnoreCase(user.getEmail())
                        && stored.getId() != user.getId());
    }
}



