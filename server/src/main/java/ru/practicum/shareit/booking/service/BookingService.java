package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> findAll(Long bookerId, String state, Integer from, Integer size);

    List<BookingDto> getOwnerBookings(Long ownerId, String state, Integer from, Integer size);
}