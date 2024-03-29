package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, CrudRepository<Booking, Long>,
        PagingAndSortingRepository<Booking, Long> {

    List<Booking> findByItemIdAndStatusOrStatusOrderByStartAsc(Long id, Status first, Status second);

    List<Booking> findByItemIdInAndStatusOrStatusOrderByStartAsc(List<Long> itemIds, Status first, Status second);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND " +
            "b.status = :status AND " +
            "(b.start BETWEEN :start AND :end OR " +
            "b.end BETWEEN :start AND :end OR " +
            "b.start <= :start AND b.end >= :end)")
    List<Booking> findBookingsAtSameTime(@Param(value = "itemId") long itemId,
                                         @Param(value = "status") Status status,
                                         @Param(value = "start") Instant start,
                                         @Param(value = "end") Instant end);

    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(Long userId, long itemId, Status status, Instant now);

    Page<Booking> findByItemOwnerId(Long userId, Pageable page);

    Page<Booking> findByItemOwnerIdAndEndIsBefore(Long userId, Instant now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsAfter(Long userId, Instant now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long userId, Instant now, Instant now1, Pageable page);

    Page<Booking> findByItemOwnerIdAndStatus(Long userId, Status status, Pageable page);

    Page<Booking> findByBookerId(Long userId, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBefore(Long userId, Instant now, Pageable page);

    Page<Booking> findByBookerIdAndStartIsAfter(Long userId, Instant now, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long userId, Instant now, Instant now1, Pageable page);

    Page<Booking> findByBookerIdAndStatus(Long userId, Status status, Pageable page);
}