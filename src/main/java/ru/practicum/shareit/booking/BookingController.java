package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.ValidationType.Create;
import ru.practicum.shareit.validation.ValidationType.Update;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @GetMapping("{bookingId}")
    public BookingOutDto findById(@RequestHeader(userIdHeader) Long userId,
                                  @PathVariable long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findByState(@RequestHeader(userIdHeader) Long userId,
                                           @RequestParam(defaultValue = "ALL") State state) {
        if (state == State.UNSUPPORTED_STATUS) {//так работает, но как то не изящно получается
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingService.findByState(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findByOwnerItemsAndState(@RequestHeader(userIdHeader) Long userId,
                                                        @RequestParam(defaultValue = "ALL") State state) {
        if (state == State.UNSUPPORTED_STATUS) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingService.findByOwnerItemsAndState(userId, state);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Create.class)
    public BookingOutDto add(@RequestHeader(userIdHeader) Long userId,
                             @Valid @RequestBody BookingInDto bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(Update.class)
    public BookingOutDto patch(@RequestHeader(userIdHeader) Long userId,
                               @PathVariable("bookingId") long bookingId,
                               @RequestParam boolean approved) {
        return bookingService.patch(userId, bookingId, approved);
    }
}