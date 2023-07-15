package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "created");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest;
    private User requester;


    @BeforeEach
    void setup() {
        requester = new User();
        requester.setName("name");
        requester.setEmail("e@mail.ru");

        itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequester(requester);
    }

    @Test
    public void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void verifyBootstrappingByPersistingRequest() {
        assertNull(itemRequest.getId());
        entityManager.persist(requester);
        entityManager.persist(itemRequest);
        assertNotNull(itemRequest.getId());
    }

    @Test
    void verifyRepositoryByPersistingRequest() {
        assertNull(itemRequest.getId());
        userRepository.save(requester);
        requestRepository.save(itemRequest);
        assertNotNull(itemRequest.getId());
    }

    @Test
    void shouldFindByRequesterId() {
        List<ItemRequest> requests = requestRepository.findByRequesterId(1L);
        assertNotNull(requests);
        assertEquals(0, requests.size());

        entityManager.persist(requester);
        entityManager.persist(itemRequest);
        requests = requestRepository.findByRequesterId(requester.getId());
        assertNotNull(requests);
        assertEquals(1, requests.size());
    }

    @Test
    void shouldFindByRequesterIdNotWithPaging() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size, SORT);

        List<ItemRequest> requests = requestRepository.findByRequesterIdNot(1L, page).getContent();
        assertNotNull(requests);
        assertEquals(0, requests.size());

        entityManager.persist(requester);
        entityManager.persist(itemRequest);
        requests = requestRepository.findByRequesterIdNot(2L, page).getContent();
        assertEquals(1, requests.size());

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("description2");
        request2.setRequester(requester);
        entityManager.persist(request2);
        requests = requestRepository.findByRequesterIdNot(2L, page).getContent();
        assertEquals(1, requests.size());
        assertEquals("description2", requests.get(0).getDescription());
    }
}