package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequestFromCreateDto(requester, itemRequestCreateDto);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        log.info("Создан новый запрос от пользователя {}", userId);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequesterId(userId);
        log.info("Запросы пользователя {}", userId);
        return itemRequestList.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestWithItemDto(itemRequest, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));

        int page = from > 0 ? from / size : 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());

        List<ItemRequest> allItemRequests = itemRequestRepository.findAll(pageable).getContent();
        log.info("Получение всех запросов");

        return allItemRequests.stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(itemRequest -> ItemRequestMapper.toItemRequestWithItemDto(itemRequest, Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getAllRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<ItemDto> proposedItems = ItemMapper.toItemProposedDtoList(
                itemRepository.findByRequestId(requestId));
        return ItemRequestMapper.toItemRequestWithItemDto(itemRequest, proposedItems);
    }
}