package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.utils.HeaderConstants.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("POST запрос вещи от пользователя с ID {}", userId);
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId) {
        log.info("GET запрос на получение данные об одном конкретном запросе c ID {}", requestId);
        return itemRequestService.getAllRequestById(requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET запрос на получение списка запросов, созданных другими пользователями.");
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("GET запрос на получение списка своих запросов вместе с данными об ответах на них.");
        return itemRequestService.getUserRequests(userId);
    }
}