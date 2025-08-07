package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @RequestBody ItemCreateDto itemCreateDto) {
        log.info("POST запрос на создание новой вещи: {} от пользователя c id: {}", itemCreateDto, userId);
        return itemService.create(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId,
                          @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("PATCH запрос на обновление вещи id: {} пользователя c id: {}", itemId, userId);
        itemUpdateDto.setId(itemId);
        return itemService.update(itemUpdateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @PathVariable("itemId") Long itemId) {
        log.info("GET запрос на получение вещи");
        return itemService.getItemDtoById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET запрос на получение всех вещей пользователя c id: {}", userId);
        return itemService.getAllItemDtoByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_ID_HEADER) Long userId, @RequestParam String text) {
        log.info("GET запрос на поиск всех вещей c текстом: {}", text);
        return itemService.searchItems(userId, text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        log.info("DELETE запрос на удаление вещи с id: {}", itemId);
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                    @Validated @RequestBody CommentCreateDto commentCreateDto,
                                    @PathVariable Long itemId) {
        log.info("POST запрос на создание нового комментария: от пользователя c id: {}", userId);
        return itemService.createComment(userId, commentCreateDto, itemId);
    }
}
