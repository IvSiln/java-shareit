package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;

public class SortUtil {
    public static final Sort DESCENDING_SORT_BY_CREATED = Sort.by(Sort.Direction.DESC, "created");
    public static final Sort DESCENDING_SORT_BY_START = Sort.by(Sort.Direction.DESC, "start");

}
