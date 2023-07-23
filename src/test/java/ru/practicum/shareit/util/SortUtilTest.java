package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class SortUtilTest {

    @Test
    public void testDescendingSortByCreated() {
        Sort expectedSort = Sort.by(Sort.Direction.DESC, "created");
        Sort actualSort = SortUtil.DESCENDING_SORT_BY_CREATED;
        assertEquals(expectedSort, actualSort);
    }

    @Test
    public void testDescendingSortByStart() {
        Sort expectedSort = Sort.by(Sort.Direction.DESC, "start");
        Sort actualSort = SortUtil.DESCENDING_SORT_BY_START;
        assertEquals(expectedSort, actualSort);
    }
}