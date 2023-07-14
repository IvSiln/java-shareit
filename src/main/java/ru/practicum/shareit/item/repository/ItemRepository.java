package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, CrudRepository<Item, Long>,
        PagingAndSortingRepository<Item, Long> {

    Page<Item> findByOwnerId(long userId, Pageable page);

    @Query(" SELECT i FROM Item i " +
            "WHERE (lower(i.name) LIKE concat('%', :text, '%') " +
            " OR lower(i.description) LIKE concat('%', :text, '%')) " +
            " AND i.available = true")
    Page<Item> searchWithPaging(@Param("text") String text, org.springframework.data.domain.Pageable page);

    List<Item> findByRequestId(long requestId);

    List<Item> findByRequestIdIn(List<Long> requestIds);
}
