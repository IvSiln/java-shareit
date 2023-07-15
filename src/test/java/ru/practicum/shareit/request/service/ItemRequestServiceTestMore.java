package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest
class ItemRequestServiceTestMore {
    private static final int SIZE_DEFAULT = 10;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAll() {
        User requester = makeUser("name", "e@mail.ru");
        entityManager.persist(requester);

        List<ItemRequestDto> sourceRequests = List.of(
                makeRequestDto("description1"),
                makeRequestDto("description2"),
                makeRequestDto("description1")
        );

        List<ItemRequest> savedRequests = new ArrayList<>();
        sourceRequests.stream()
                .map(RequestMapper::toItemRequest)
                .forEach(request -> {
                    request.setRequester(requester);
                    savedRequests.add(requestRepository.save(request));
                });

        User owner = makeUser("name1", "e1@mail.ru");
        entityManager.persist(owner);

        ItemRequest request = savedRequests.get(0);
        Item item = makeAvailableItem("name", "description", owner, request);
        entityManager.persist(item);

        entityManager.flush();

        List<ItemRequestDto> targetRequests = requestService.findAll(owner.getId(), 0, SIZE_DEFAULT);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription()))
            )));
        }
        assertThat(targetRequests, hasItem(
                hasProperty("items", notNullValue())
        ));
    }



    private ItemRequestDto makeRequestDto(String description) {
        return ItemRequestDto.builder()
                .description(description)
                .build();
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Item makeAvailableItem(String name, String description, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
        return itemRepository.save(item);
    }
}
