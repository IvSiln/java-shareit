package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User owner;
    private Item item;
    private User requester;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setName("name");
        owner.setEmail("e@mail.ru");
        owner = userRepository.save(owner);

        requester = new User();
        requester.setName("name1");
        requester.setEmail("e1@mail.ru");
        requester = userRepository.save(requester);

        request = new ItemRequest();
        request.setDescription("description");
        request.setRequester(requester);
        request = requestRepository.save(request);

        item = new Item();
        item.setName("Набор отверток");
        item.setDescription("Большой набор отверток");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
        item = itemRepository.save(item);
    }

    @Test
    public void contextLoads() {
        assertNotNull(entityManager);
    }

    @Test
    void findByOwnerId() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size);

        //Empty List
        List<Item> items = itemRepository.findByOwnerId(0L, page).getContent();
        assertNotNull(items);
        assertEquals(0, items.size());

        //Single List
        items = itemRepository.findByOwnerId(owner.getId(), page).getContent();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());

        //With Paging
        Item item1 = new Item();
        item1.setOwner(owner);
        item1.setAvailable(true);
        item1.setName("Дрель");
        item1.setDescription("Дрель — ваш ответ соседям с перфоратором");
        itemRepository.save(item1);

        items = itemRepository.findByOwnerId(owner.getId(), page).getContent();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());

        pageNum = 1;
        page = PageRequest.of(pageNum, size);
        items = itemRepository.findByOwnerId(owner.getId(), page).getContent();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item1.getId(), items.get(0).getId());
    }

    @Test
    void search() {
        int pageNum = 0;
        int size = 1;
        PageRequest page = PageRequest.of(pageNum, size);

        //Empty List
        String text = "фыва";
        TypedQuery<Item> query = entityManager.getEntityManager()
                .createQuery(" select i from Item i " +
                        "where (lower(i.name) like concat('%', :text, '%') " +
                        " or lower(i.description) like concat('%', :text, '%')) " +
                        " and i.available = true", Item.class);
        List<Item> items = query.setParameter("text", text).getResultList();
        assertEquals(0, items.size());
        List<Item> itemsSearch = itemRepository.searchWithPaging(text, page).getContent();
        assertNotNull(itemsSearch);
        assertEquals(0, itemsSearch.size());

        //Single List
        text = "отв";
        items = query.setParameter("text", text).getResultList();
        assertEquals(1, items.size());
        itemsSearch = itemRepository.searchWithPaging(text, page).getContent();
        assertNotNull(itemsSearch);
        assertEquals(1, itemsSearch.size());
        assertEquals(items.get(0).getId(), itemsSearch.get(0).getId());

        //With Paging
        Item item1 = new Item();
        item1.setOwner(owner);
        item1.setAvailable(true);
        item1.setName("Дрель");
        item1.setDescription("Дрель — ваш ответ соседям с перфоратором");
        itemRepository.save(item1);

        itemsSearch = itemRepository.searchWithPaging(text, page).getContent();
        assertNotNull(itemsSearch);
        assertEquals(1, itemsSearch.size());

        size = 2;
        page = PageRequest.of(pageNum, size);
        itemsSearch = itemRepository.searchWithPaging(text, page).getContent();
        assertNotNull(itemsSearch);
        assertEquals(2, itemsSearch.size());
    }

    @Test
    void findByRequestId() {
        //Empty List
        List<Item> items = itemRepository.findByRequestId(0L);
        assertNotNull(items);
        assertEquals(0, items.size());

        //Single List
        items = itemRepository.findByRequestId(request.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }

    @Test
    void findByRequestIdIn() {
        //Empty List
        List<Item> items = itemRepository.findByRequestIdIn(List.of(0L));
        assertNotNull(items);
        assertEquals(0, items.size());

        //Single List
        items = itemRepository.findByRequestIdIn(List.of(request.getId()));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
    }
}