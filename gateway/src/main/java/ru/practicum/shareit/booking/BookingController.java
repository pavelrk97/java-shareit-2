package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.utils.Constants;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("POST запрос на создание нового бронирования вещи от пользователя c id: {} ", userId);
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                         @PathVariable Long bookingId,
                                         @RequestParam Boolean approved) {
        log.info("PATCH запрос на обновление бронирования вещи от пользователя c id: {} ", userId);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET запрос на получение списка всех бронирований текущего пользователя с id: {} и статусом {}, от={}, до={}", userId, stateParam, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwner(@RequestHeader(Constants.USER_ID_HEADER) Long ownerId,
                                              @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(bookingState)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingState));
        log.info("GET запрос на получение списка всех бронирований текущего владельца с id: {} и статусом {}, от={}, до={}", ownerId, bookingState, from, size);
        return bookingClient.getAllOwner(ownerId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Constants.USER_ID_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("GET запрос на получение данных о  бронировании {} от пользователя с id: {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }
}
