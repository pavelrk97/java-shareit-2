package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingClientTest {

    @Mock
    private BookingClient bookingClient;

    @Test
    void createBooking() {
        Long userId = 1L;
        BookItemRequestDto requestDto = new BookItemRequestDto();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Бронь создана", HttpStatus.OK);

        when(bookingClient.create(userId, requestDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.create(userId, requestDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Информация о бронирование обновлена", HttpStatus.OK);

        when(bookingClient.update(userId, bookingId, approved)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.update(userId, bookingId, approved);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBookings() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Бронирования", HttpStatus.OK);

        when(bookingClient.getBookings(userId, state, from, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getBookings(userId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getAllOwner() {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Владелец бронирований", HttpStatus.OK);

        when(bookingClient.getAllOwner(ownerId, state, from, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getAllOwner(ownerId, state, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Бронирование", HttpStatus.OK);

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = bookingClient.getBooking(userId, bookingId);
        assertEquals(expectedResponse, actualResponse);
    }
}
