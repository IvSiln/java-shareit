package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    /**
     * Returns the User with the specified ID.
     * @param id the ID of the User
     * @return the User object
     */
    User get(Long id);

    /**
     * Returns a collection of all Users.
     * @return a collection of User objects
     */
    Collection<User> getAll();

    /**
     * Adds a User to the storage.
     * @param user the User object to be added
     * @return the added User object in the storage
     */
    User add(User user);

    /**
     * Updates the fields of a User.
     * @param user the User object with the changes
     * @return the updated User object
     */
    User patch(User user);

    /**
     * Deletes a User from the storage.
     * @param id the ID of the User to be deleted
     * @return true if the deletion is successful, false otherwise
     */
    boolean delete(Long id);
}