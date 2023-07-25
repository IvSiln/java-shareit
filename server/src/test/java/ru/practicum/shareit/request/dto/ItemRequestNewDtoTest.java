package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemRequestNewDtoTest {

    @Test
    public void testValidItemRequestNewDto() {
        ItemRequestNewDto itemRequestNewDto = ItemRequestNewDto.builder()
                .id(1L)
                .description("Valid description")
                .created(LocalDateTime.now())
                .build();

        assertThat(itemRequestNewDto.getId()).isEqualTo(1L);
        assertThat(itemRequestNewDto.getDescription()).isEqualTo("Valid description");
        assertThat(itemRequestNewDto.getCreated()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    public void testEmptyDescription() {
        ItemRequestNewDto itemRequestNewDto = ItemRequestNewDto.builder()
                .id(1L)
                .description("")
                .created(LocalDateTime.now())
                .build();

        assertThat(itemRequestNewDto.getDescription()).isEmpty();
    }
}