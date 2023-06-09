package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    /**
     * Возвращает DTO Вещи по идентификатору
     *
     * @param id идентификатор вещи
     * @return ItemDto
     */
    ItemDto getItem(Long id);

    /**
     * Возвращает коллекцию DTO Вещей Пользователя
     *
     * @param userId идентификатор Пользователя владельца Вещи
     * @return коллекцию ItemDto
     */
    List<ItemDto> getAllItemsByUserId(Long userId);

    /**
     * Реализует добавление Вещи в хранилище
     *
     * @param itemDto DTO объект Вещи
     * @param ownerId идентификатор Пользователя владельца
     * @return DTO добавленного объекта Item в хранилище
     */
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    /**
     * Реализует обновление полей хранимой Вещи
     *
     * @param itemDto объект Вещи с изменениями
     * @param itemId  идентификатор Вещи
     * @param userId  идентификатор Пользователя
     * @return DTO обновленного объекта Item
     */
    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    /**
     * Реализует удаление Вещи из хранилища
     *
     * @param id идентификатор удаляемой вещи
     */
    void removeItem(Long id);

    /**
     * Реализует поиск Вещей в хранилище по ключевому слову
     *
     * @param keyword ключевое слово для поиска
     * @return коллекцию DTO объектов Item
     */
    Collection<ItemDto> searchItemsByDescription(String keyword);
}
