package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto getItem(Long id) {
        itemIdValidator(itemStorage.getItem(id));
        return itemMapper.toItemDto(itemStorage.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = itemMapper.toItem(itemDto);
        User owner = userStorage.get(userId);
        itemOwnerCheckValidator(owner, newItem, userId);
        Item createdItem = itemStorage.createItem(newItem);
        return itemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item item = itemMapper.toItem(itemDto);
        userIdValidator(userId);
        Item oldItem = itemStorage.getItem(itemId);
        itemOwnerNameDescAvailValidator(item, oldItem, userId);
        Item changedItem = itemStorage.updateItem(oldItem);
        return itemMapper.toItemDto(changedItem);
    }

    public void removeItem(Long id) {
        itemIdValidator(itemStorage.getItem(id));
        itemStorage.removeItem(id);
    }

    @Override
    public Collection<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getAllItems()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void itemIdValidator(Item item) {
        if (!itemStorage.getAllItems().contains(itemStorage.getItem(item.getId()))) {
            throw new NotFoundException("Item with id " + itemStorage.getItem(item.getId()) + "not found");
        }
        if (item.getName().isBlank()) {
            throw new NotValidException("Name cant be blank");
        }
        if (item.getDescription().isBlank()) {
            throw new NotValidException("Description cant be blank");
        }
    }

    private void itemOwnerCheckValidator(User owner, Item newItem, long id) {
        if (owner == null) {
            throw new NotFoundException(String.format("User with id=%d not found", id));
        } else {
            newItem.setOwner(owner);
        }
    }

    private void itemOwnerNameDescAvailValidator(Item item, Item oldItem, long userId) {
        if (oldItem.getOwner().getId() != userId) {
            throw new NotFoundException("User is not owner of this item!");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
    }

    private void userIdValidator(Long userId) {
        if (!userStorage.getAll().contains(userStorage.get(userId))) {
            throw new NotFoundException(String.format("user with id = %d not found.", userId));
        }
    }
}
