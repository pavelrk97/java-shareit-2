package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    Item update(Item oldItem);

    Item getItemById(Long itemId);

    Collection<Item> findAll();

    List<Item> searchItems(String text);

    void deleteItem(Long itemId);
}
