package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.ValidationType;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;

    @GetMapping("{bookingId}")
    public BookingOutDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable long bookingId) {
        return service.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL") State state,
                                           @RequestParam(defaultValue = "0") @Min(value = 0) int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        return service.findByState(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findByOwnerItemsAndState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "ALL") State state,
                                                        @RequestParam(defaultValue = "0")
                                                        @Min(value = 0) int from,
                                                        @RequestParam(defaultValue = "10") @Positive int size) {
        return service.findByOwnerItemsAndState(userId, state, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationType.Create.class)
    public BookingOutDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingInDto bookingDto) {
        return service.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(ValidationType.Update.class)
    public BookingOutDto patch(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable("bookingId") long bookingId,
                               @RequestParam boolean approved) {
        return service.patch(userId, bookingId, approved);
    }
}
