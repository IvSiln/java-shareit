package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.validation.ValidationType.Create;
import static ru.practicum.shareit.validation.ValidationType.Update;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemBookingCommentsDto> findAllByUserId(@RequestHeader(userIdHeader) long userId) {
        return itemService.findAllByUserId(userId);
    }

    @GetMapping("{itemId}")
    public ItemBookingCommentsDto findById(@RequestHeader(userIdHeader) long userId,
                                           @PathVariable long itemId) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Create.class)
    public ItemDto add(@RequestHeader(userIdHeader) Long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated(Update.class)
    public ItemDto patch(@RequestHeader(userIdHeader) Long userId,
                         @Valid @RequestBody ItemDto itemDto,
                         @PathVariable("itemId") long itemId) {
        return itemService.patch(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(userIdHeader) long userId,
                           @PathVariable long itemId) {
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader(userIdHeader) Long userId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @PathVariable("itemId") long itemId
    ) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}