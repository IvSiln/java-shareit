package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long increment = 0L;

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        user.setId(++increment);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User patch(User user) {
        User patchedUser = users.computeIfPresent(user.getId(), (key, value) -> {
            if (user.getName() != null && !user.getName().isEmpty()) {
                value.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                value.setEmail(user.getEmail());
            }
            return value;
        });
        return patchedUser;
    }


    @Override
    public boolean delete(Long id) {
        users.remove(id);
        return !users.containsKey(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}