package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private static final String USER_NOT_FOUND = "Пользователь не найден";


    @Override
    public BookingDto create(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена."));
        validateBookingCreate(item, bookingCreateDto);
        Booking booking = BookingMapper.toBookingFromCreateDto(bookingCreateDto, item, booker);

        booking.setItem(item);
        booking.setBooker(booker);

        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " + bookingId + " не найдено."));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Только владелец вещи может подтвердить или отклонить бронирование.");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalStateException("Статус можно изменить только у бронирования со статусом WAITING.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование " + bookingId + " не найдено."));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Просматривать бронирование может только владелец вещи или создатель брони.");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAll(Long bookerId, String state, Integer from, Integer size) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        State bookingState = State.parseState(state);
        LocalDateTime now = LocalDateTime.now();

        int page = from > 0 ? from / size : 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case CURRENT ->
                    bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatusAndStartAfterOrderByStartDesc(bookerId, Status.WAITING, now);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        State bookingState = State.parseState(state);
        LocalDateTime now = LocalDateTime.now();

        int page = from > 0 ? from / size : 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING ->
                    bookingRepository.findByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(ownerId, Status.WAITING, now);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    private void checkIfBookingTimeIsAvailable(Item item, LocalDateTime start, LocalDateTime end) {

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(item.getId(), start, end);

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("Данное время уже занято для бронирования.");
        }
    }

    private void bookingValidation(BookingCreateDto bookingCreateDto, Item item) {
        if (!item.getAvailable()) {
            throw new IllegalStateException("Вещь недоступна для бронирования.");
        }

        if (!bookingCreateDto.getStart().isBefore(bookingCreateDto.getEnd())) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени окончания.");
        }
    }

    private void validateBookingCreate(Item item, BookingCreateDto bookingCreateDto) {
        if (!item.getAvailable()) {
            throw new IllegalStateException("Вещь недоступна для бронирования.");
        }

        if (!bookingCreateDto.getStart().isBefore(bookingCreateDto.getEnd())) {
            throw new IllegalArgumentException("Время начала должно быть раньше времени окончания.");
        }

        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                item.getId(),
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("Данное время уже занято для бронирования.");
        }
    }
}