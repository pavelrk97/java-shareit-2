package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.utils.Constants;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("POST запрос на создание новой вещи: {} от пользователя c id: {}", itemCreateDto, userId);
        return itemClient.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                         @RequestBody ItemUpdateDto itemDto,
                                         @PathVariable("itemId") Long itemId) {
        log.info("PATCH запрос на обновление вещи id: {} пользователя c id: {}", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(Constants.USER_ID_HEADER) Long requesterId,
                                               @PathVariable Long itemId) {
        log.info("GET запрос на получение вещи");
        return itemClient.findItemById(itemId, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(Constants.USER_ID_HEADER) Long userId) {
        log.info("GET запрос на получение всех вещей пользователя c id: {}", userId);
        return itemClient.findAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @RequestParam String text) {
        log.info("GET запрос на поиск всех вещей c текстом: {}", text);
        return itemClient.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable Long itemId) {
        log.info("DELETE запрос на удаление вещи с id: {}", itemId);
        return itemClient.deleteById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                                @Valid @RequestBody CommentRequestDto commentDto,
                                                @PathVariable Long itemId) {
        log.info("POST запрос на создание нового комментария: от пользователя c id: {}", userId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}