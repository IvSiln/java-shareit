package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    /**
     * Retrieves an item by its ID.
     *
     * @param id the ID of the item
     * @return the item object
     */
    Item getItem(Long id);

    /**
     * Retrieves a collection of all items.
     *
     * @return a collection of item objects
     */
    List<Item> getAllItems();

    /**
     * Adds an item to the storage.
     *
     * @param item the item object to be added
     * @return the added item object
     */
    Item createItem(Item item);

    /**
     * Updates the fields of an existing item.
     *
     * @param item the item object with the changes
     * @return the updated item object
     */
    Item updateItem(Item item);

    /**
     * Removes an item from the storage.
     *
     * @param id the ID of the item to be removed
     */
    void removeItem(Long id);
}
