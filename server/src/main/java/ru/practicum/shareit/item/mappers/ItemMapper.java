package ru.practicum.shareit.item.mappers;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mappers.UserMapper;

import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {

    public static Item toItemFromCreateDto(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public static ItemDto toItemFromDto(Item item) {
        Long requestId = (item.getRequest() != null) ? item.getRequest().getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .requestId(requestId)
                .build();
    }

    public static List<ItemDto> toItemProposedDtoList(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemFromDto)
                .collect(Collectors.toList());
    }
}