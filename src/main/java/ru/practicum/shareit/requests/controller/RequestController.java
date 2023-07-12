package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestNewDto;
import ru.practicum.shareit.requests.service.RequestService;
import ru.practicum.shareit.validation.ValidationType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationType.Create.class)
    public ItemRequestNewDto addRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemRequestNewDto itemRequestNewDto
    ) {
        return service.add(userId, itemRequestNewDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(value = 0) int from,
            @RequestParam(defaultValue = "10")
            @Positive int size) {
        return service.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable long requestId) {
        return service.findById(userId, requestId);
    }
}
