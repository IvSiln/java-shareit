package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Booking booking;
    private Item item;

    @BeforeEach
    void setup() {
        User owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("name1");
        booker.setEmail("e1@mail.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Лошадь");
        item.setDescription("Это не дура, это лошадь");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Instant now = Instant.now();
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(now.plusSeconds(5));
        booking.setEnd(now.plusSeconds(60));
        booking.setStatus(Status.APPROVED);
        booking = bookingRepository.save(booking);
    }

    @Test
    public void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void findBookingsAtSameTime() {
        Instant start = booking.getEnd().plusSeconds(5);
        Instant end = booking.getEnd().plusSeconds(25);
        Status status = Status.APPROVED;
        TypedQuery<Booking> query = entityManager.getEntityManager().createQuery("SELECT b FROM Booking b " +
                "WHERE (b.item.id = :itemId) AND " +
                "(b.status = :status) AND " +
                "((b.start BETWEEN :start AND :end) OR " +
                "(b.end BETWEEN :start AND :end) OR " +
                "(b.start <= :start AND b.end >= :end))", Booking.class);
        List<Booking> bookings = query
                .setParameter("itemId", item.getId())
                .setParameter("status", status)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        assertNotNull(bookings);
        assertEquals(0, bookings.size());
        List<Booking> bookingsFound = bookingRepository.findBookingsAtSameTime(item.getId(), status, start, end);
        assertNotNull(bookingsFound);
        assertEquals(0, bookingsFound.size());

        start = booking.getStart().plusSeconds(5);
        bookings = query
                .setParameter("itemId", item.getId())
                .setParameter("status", status)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        bookingsFound = bookingRepository.findBookingsAtSameTime(item.getId(), status, start, end);
        assertNotNull(bookingsFound);
        assertEquals(1, bookingsFound.size());
        assertEquals(bookings.get(0).getId(), bookingsFound.get(0).getId());
    }
}