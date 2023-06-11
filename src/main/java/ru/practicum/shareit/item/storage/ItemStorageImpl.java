package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long itemId = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(++itemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllItemsById(Long userId) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public void removeItem(Long id) {
        items.remove(id);
    }
}
