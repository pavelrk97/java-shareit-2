package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest {

    private ItemRepositoryImpl itemRepository;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepositoryImpl();

        item1 = Item.builder().id(1L).name("Item 1").description("Description 1").available(true).owner(1L).build();
        item2 = Item.builder().id(2L).name("Item 2").description("Description 2").available(true).owner(1L).build();
        itemRepository.create(item1);
        itemRepository.create(item2);
    }

    @Test
    void returnItemWhenItemExistsTest() {
        Item retrievedItem = itemRepository.getItemById(1L);
        assertEquals(item1, retrievedItem);
    }

    @Test
    void returnWhenItemDoesNotExistTest() {
        assertThrows(NotFoundException.class, () -> itemRepository.getItemById(99L));
    }

    @Test
    void returnAllItemsTest() {
        Collection<Item> allItems = itemRepository.findAll();
        assertEquals(2, allItems.size());
        assertTrue(allItems.contains(item1));
        assertTrue(allItems.contains(item2));
    }

    @Test
    void returnMatchingItemsTest() {
        List<Item> searchResults = itemRepository.searchItems("Item 1");
        assertEquals(1, searchResults.size());
        assertTrue(searchResults.contains(item1));
    }

    @Test
    void returnEmptyListWhenNoMatchTest() {
        List<Item> searchResults = itemRepository.searchItems("NonExistent");
        assertTrue(searchResults.isEmpty());
    }
}
