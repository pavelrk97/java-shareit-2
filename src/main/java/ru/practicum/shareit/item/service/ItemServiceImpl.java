package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {

        Optional.ofNullable(userRepository.findUserById(userId)).orElseThrow(() ->
                new NotFoundException("User not found " + userId));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item createdItem = itemRepository.create(item);
        log.info("Item created: " + createdItem);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        Long itemId = itemDto.getId();

        Item itemFromDto = ItemMapper.toItem(itemDto);

        Item oldItem = itemRepository.getItemById(itemId);

        Optional.ofNullable(oldItem).orElseThrow(() ->
                new NotFoundException("Item not found id = " + itemId));

        if (!Objects.equals(oldItem.getOwner(), userId)) {
            throw new NotFoundException("You are not owner of this item");
        }

        Optional.ofNullable(itemFromDto.getName()).filter(s -> !s.isBlank())
                .ifPresent(oldItem::setName);  // проверка на ввод пробела и налл
        Optional.ofNullable(itemFromDto.getDescription()).ifPresent(oldItem::setDescription);
        Optional.ofNullable(itemFromDto.getAvailable()).ifPresent(oldItem::setAvailable);

        Item updatedItem = itemRepository.update(oldItem);
        log.info("Item updated: " + updatedItem.getId());
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemDtoById(Long itemId) {
        Item item = Optional.ofNullable(itemRepository.getItemById(itemId)).orElseThrow(() ->
                new NotFoundException("Item not found " + itemId));
        log.info("Item found: " + item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemDtoByUserId(Long userId) {
        return itemRepository.findAll().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isEmpty()) {
            log.info("text is null or empty");
            return List.of();
        }
        List<Item> items = itemRepository.searchItems(text);
        log.info("Поиск предметов по тексту: {}", text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
        log.info("Удален предмет с ID: {}", itemId);
    }
}