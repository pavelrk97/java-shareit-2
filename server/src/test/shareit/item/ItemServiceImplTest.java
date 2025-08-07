package shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Test").email("Test@yandex.ru").build();
        itemCreateDto = ItemCreateDto.builder().name("TestItem").description("For test").available(true).build();
        item = Item.builder().id(1L).name("TestItem").description("For test").available(true).owner(user).build();
        itemUpdateDto = ItemUpdateDto.builder().build();
    }

    @Test
    void createItemReturnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDto = itemService.create(1L, itemCreateDto);

        assertEquals(itemCreateDto.getName(), itemDto.getName());
        assertEquals(itemCreateDto.getDescription(), itemDto.getDescription());
        assertEquals(itemCreateDto.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void createItemWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(1L, itemCreateDto);
        });

        assertEquals("Пользователь с ID 1 не найден.", exception.getMessage());
    }

    @Test
    void getItemDtoByIdItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

        assertThrows(NotFoundException.class, () -> itemService.getItemDtoById(1L, 1L));
    }

    @Test
    void getItemByIdNotFoundTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        when(userRepository.findById(1L)).thenReturn(Optional.of(User.builder().id(1L).build()));

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemDtoById(1L, 1L);
        });
    }

    @Test
    void updateItemWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder().build();

        assertThrows(NotFoundException.class, () -> itemService.update(itemUpdateDto, 1L));
    }

    @Test
    void updateItemSuccess() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated");
        itemUpdateDto.setDescription("Updated Description");
        itemUpdateDto.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto updatedItemDto = itemService.update(itemUpdateDto, 1L);

        assertEquals(itemUpdateDto.getName(), updatedItemDto.getName());
        assertEquals(itemUpdateDto.getDescription(), updatedItemDto.getDescription());
        assertEquals(itemUpdateDto.getAvailable(), updatedItemDto.getAvailable());
    }

    @Test
    void getItemDtoByIdTest() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemAndStatusOrderByStartAsc(any(), any())).thenReturn(Collections.emptyList());

        ItemDto itemDto = itemService.getItemDtoById(1L, 1L);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void getAllItemDtoByUserIdUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getAllItemDtoByUserId(1L);
        });

        assertEquals("Пользователь с ID 1 не найден.", exception.getMessage());
    }

    @Test
    void getAllItemDtoByUserId_validUserId_returnsCollectionOfItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(any(User.class))).thenReturn(Collections.singletonList(item));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        Collection<ItemDto> result = itemService.getAllItemDtoByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchItemsEmptyText() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<ItemDto> result = itemService.searchItems(1L, "");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemsBlankText() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> result = itemService.searchItems(1L, "   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemsNullText() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<ItemDto> result = itemService.searchItems(1L, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemsValidTextReturnsListOfItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.searchItems(anyString())).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.searchItems(1L, "test");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void createComment_validInput_returnsCommentDto() {
        Booking booking = Booking.builder().id(1L).build();
        CommentCreateDto commentCreateDto = CommentCreateDto.builder().text("TestComment").build();
        Comment comment = Comment.builder().id(1L).text("TestComment").item(item).author(user).build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Collections.singletonList(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(1L, commentCreateDto, 1L);

        assertNotNull(result);
        assertEquals(commentCreateDto.getText(), result.getText());
    }
}