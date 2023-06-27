package ru.practicum.shareit.booking.dto;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MinDuration {
    String duration() default "PT1H";
    String message() default "Minimum booking duration is 1 hour";
}
