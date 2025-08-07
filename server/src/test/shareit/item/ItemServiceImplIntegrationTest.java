package shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createItemReturnItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto itemDto = itemService.create(savedUser.getId(), itemCreateDto);

        assertNotNull(itemDto.getId());
        assertEquals(itemCreateDto.getName(), itemDto.getName());
        assertEquals(itemCreateDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void getItemDtoByIdReturnItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        ItemDto createdItemDto = itemService.create(savedUser.getId(), itemCreateDto);

        ItemDto itemDto = itemService.getItemDtoById(createdItemDto.getId(), savedUser.getId());

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals(createdItemDto.getName(), itemDto.getName());
        assertEquals(itemCreateDto.getDescription(), itemDto.getDescription());
    }

    @Test
    void searchItemsReturnListOfItemDto() {
        User user = new User();
        user.setName("testUser");
        user.setEmail("test@yandex.ru");
        User savedUser = userRepository.save(user);

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description with keyword");
        itemCreateDto.setAvailable(true);

        itemService.create(savedUser.getId(), itemCreateDto);

        List<ItemDto> searchResults = itemService.searchItems(savedUser.getId(), "keyword");

        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
    }
}