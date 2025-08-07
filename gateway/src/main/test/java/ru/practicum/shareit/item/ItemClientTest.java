package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private ItemClient itemClient;

    @Test
    void createItemTest() {
        Long userId = 1L;
        ItemCreateDto itemDto = new ItemCreateDto();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Товар добавлен", HttpStatus.OK);

        when(itemClient.create(userId, itemDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.create(userId, itemDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateItemTest() {
        Long userId = 1L;
        Long itemId = 1L;
        ItemUpdateDto itemDto = new ItemUpdateDto();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Информация о товаре обновлена", HttpStatus.OK);

        when(itemClient.update(userId, itemId, itemDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.update(userId, itemId, itemDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findItemByIdTest() {
        Long itemId = 1L;
        Long requesterId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Товар найден", HttpStatus.OK);

        when(itemClient.findItemById(itemId, requesterId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.findItemById(itemId, requesterId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void searchItemsTest() {
        Long userId = 1L;
        String text = "test";
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Товар", HttpStatus.OK);

        when(itemClient.searchItems(userId, text)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.searchItems(userId, text);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void createCommentTest() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentRequestDto commentDto = new CommentRequestDto();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Комментарий создан", HttpStatus.OK);

        when(itemClient.createComment(userId, commentDto, itemId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = itemClient.createComment(userId, commentDto, itemId);
        assertEquals(expectedResponse, actualResponse);
    }
}