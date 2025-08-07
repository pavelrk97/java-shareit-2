package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemCreateDto itemCreateDto);

    ItemDto update(ItemUpdateDto itemUpdateDto, Long userId);

    ItemDto getItemDtoById(Long userId, Long itemId);

    Collection<ItemDto> getAllItemDtoByUserId(Long userId);

    List<ItemDto> searchItems(Long userId, String text);

    void deleteItem(Long itemId);

    CommentDto createComment(Long userId, CommentCreateDto commentCreateDto, Long itemId);

    List<CommentDto> getItemComments(Long itemId);
}
