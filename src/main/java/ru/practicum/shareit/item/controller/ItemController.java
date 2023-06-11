package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(userIdHeader) Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItemsByDescription(text);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@PathVariable Long itemId) {
        itemService.remove(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        Optional<ItemDto> optionalItemDto = itemService.getItem(itemId);
        return optionalItemDto.orElseThrow(() -> new NotFoundException("Item not found."));
    }

    @GetMapping()
    public List<ItemDto> findAll(@RequestHeader(userIdHeader) Long userId) {
        return itemService.getAllByUserId(userId);
    }
}
