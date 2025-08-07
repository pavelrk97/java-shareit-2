package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    ItemDto getItemDtoById(Long itemId);

    Collection<ItemDto> getAllItemDtoByUserId(Long userId);

    List<ItemDto> searchItems(String text);

    void deleteItem(Long itemId);
}
