package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
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

    private final ObjectMapper mapper = JsonMapper.builder()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
            .build();


    @GetMapping("{bookingId}")
    public BookingOutDto findById(@RequestHeader(userIdHeader) Long userId,
                                  @PathVariable long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> findByState(@RequestHeader(userIdHeader) Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) throws JsonProcessingException {
        State stateEnum = mapper.readValue(mapper.writeValueAsString(state), State.class);
        return bookingService.findByState(userId, stateEnum);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> findByOwnerItemsAndState(@RequestHeader(userIdHeader) Long userId,
                                                        @RequestParam(defaultValue = "ALL") String state
    ) throws JsonProcessingException {
        State stateEnum = mapper.readValue(mapper.writeValueAsString(state), State.class);
        return bookingService.findByOwnerItemsAndState(userId, stateEnum);
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