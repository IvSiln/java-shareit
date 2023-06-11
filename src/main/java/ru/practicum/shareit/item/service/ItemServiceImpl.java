package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Optional<ItemDto> getItem(Long id) {
        Optional<Item> item = itemStorage.findItemById(id);
        //  validateItemDataAndId(item);
        return item.map(itemMapper::toItemDto);
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        return itemStorage.findAllItemsById(userId).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        Optional<User> owner = userStorage.get(userId);
        return owner.map(user -> {
                    Item newItem = itemMapper.toItem(itemDto);
                    setOwnerIfValid(user, newItem);
                    Item createdItem = itemStorage.createItem(newItem);
                    return itemMapper.toItemDto(createdItem);
                })
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }


    @Override
    public ItemDto update(ItemDto itemDto, long itemId, long userId) {
        Optional<Item> oldItem = itemStorage.findItemById(itemId);
        if (!isItemOwnedByUser(oldItem, userId)) {
            throw new NotFoundException("User is not owner of this item!");
        }
        Item updatedItem = updateItemFields(oldItem, itemDto);
        Item savedItem = itemStorage.updateItem(updatedItem);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public void remove(Long id) {
        //validateItemDataAndId(itemStorage.findItemById(id));
        itemStorage.removeItem(id);
    }

    @Override
    public List<ItemDto> searchItemsByDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.findAllItems().stream().filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()).map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    private boolean isItemOwnedByUser(Optional<Item> item, long userId) {
        return item.map(Item::getOwner)
                .map(User::getId)
                .filter(ownerId -> ownerId.equals(userId))
                .isPresent();
    }

    private Item updateItemFields(Optional<Item> item, ItemDto newItemDto) {
        Item oldItem = item.orElseThrow(() -> new NotFoundException("Item not found"));
        modifyItem(oldItem, newItemDto.getName(), newItemDto.getDescription(), newItemDto.getAvailable());
        return oldItem;
    }

    private void modifyItem(Item item, String name, String description, Boolean available) {
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (available != null) {
            item.setAvailable(available);
        }
    }

    private void validateItemDataAndId(Optional<Item> item) {
        if (!item.isPresent()) {
            throw new NotFoundException("Item not found");
        }
        if (item.get().getName().isBlank()) {
            throw new BadRequestException("Name can't be blank");
        }
        if (item.get().getDescription().isBlank()) {
            throw new BadRequestException("Description can't be blank");
        }
    }

    private void setOwnerIfValid(User owner, Item newItem) {
        Optional.ofNullable(owner).map(user -> {
            newItem.setOwner(user);
            return newItem;
        }).orElseThrow(() -> new NotFoundException("User not found"));
    }
}
