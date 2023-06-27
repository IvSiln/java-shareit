package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.validation.ValidationType.Create;
import ru.practicum.shareit.validation.ValidationType.Update;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingInDto {
    private Long id;

    @NotNull(groups = Create.class, message = "The booking start date cannot be empty")
    @FutureOrPresent(message = "The booking start date cannot be in the past")
    private LocalDateTime start;

    @NotNull(groups = Create.class, message = "The end date of the booking cannot be empty")
    @FutureOrPresent(message = "The end date of the booking cannot be in the past")
    private LocalDateTime end;

    @NotNull(groups = Create.class, message = "Item not specified")
    private Long itemId;

    @NotNull(groups = Update.class, message = "The status cannot be empty")
    private Status status;
}
