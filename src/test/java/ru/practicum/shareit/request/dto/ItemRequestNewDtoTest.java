package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemRequestNewDtoTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    public void testValidItemRequestNewDto() {
        ItemRequestNewDto itemRequestNewDto = ItemRequestNewDto.builder()
                .id(1L)
                .description("Valid description")
                .created(LocalDateTime.now())
                .build();

        assertTrue(validator.validate(itemRequestNewDto).isEmpty());
    }

    @Test
    public void testEmptyDescription() {
        ItemRequestNewDto itemRequestNewDto = ItemRequestNewDto.builder()
                .id(1L)
                .description("")
                .created(LocalDateTime.now())
                .build();

        assertEquals(1, validator.validate(itemRequestNewDto).size());
    }
}
