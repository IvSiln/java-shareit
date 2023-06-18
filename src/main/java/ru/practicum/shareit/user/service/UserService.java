package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    /**
     * Returns the User DTO by its identifier.
     *
     * @param id the identifier of the user
     * @return UserDto
     */
    Optional<UserDto> get(Long id);

    /**
     * Returns a collection of User DTOs.
     *
     * @return collection of UserDto
     */
    Collection<UserDto> getAll();

    /**
     * Adds a User to the storage.
     *
     * @param userDto DTO object of the user
     * @return DTO of the added UserDto object in the storage
     */
    UserDto add(UserDto userDto);

    /**
     * Updates the fields of a User.
     *
     * @param userDto the User object with changes
     * @param id      the identifier of the User
     * @return DTO of the updated UserDto object
     */
    UserDto patch(UserDto userDto, Long id);

    /**
     * Deletes a User from the storage.
     *
     * @param id the identifier of the User
     * @return true if deletion is successful
     */
    boolean delete(Long id);
}
