package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemBookingCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapperTest {
    @Test
    void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test_Name");
        item.setDescription("Test_Description");
        item.setAvailable(true);
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        item.setRequest(request);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void testToItemBookingCommentsDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test_Name");
        item.setDescription("Test_Description");
        item.setAvailable(true);
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        item.setRequest(request);

        ItemBookingCommentsDto itemBookingCommentsDto = ItemMapper.toItemBookingCommentsDto(item);

        Assertions.assertEquals(item.getId(), itemBookingCommentsDto.getId());
        Assertions.assertEquals(item.getName(), itemBookingCommentsDto.getName());
        Assertions.assertEquals(item.getDescription(), itemBookingCommentsDto.getDescription());
        Assertions.assertEquals(item.getRequest().getId(), itemBookingCommentsDto.getRequestId());
    }

    @Test
    void testToItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test_Name")
                .description("Test_Description")
                .available(true)
                .requestId(2L)
                .build();
        User owner = new User();

        Item item = ItemMapper.toItem(itemDto, owner);

        Assertions.assertEquals(itemDto.getId(), item.getId());
        Assertions.assertEquals(itemDto.getName(), item.getName());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(owner, item.getOwner());
        Assertions.assertEquals(itemDto.getRequestId(), item.getRequest().getId());
    }
}
