package shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks

    private BookingServiceImpl bookingService;
    private User booker;
    private User owner;
    private Item item;
    private BookingCreateDto bookingCreateDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Booker");
        booker.setEmail("booker@yandex.ru");

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@yandex.ru");

        item = new Item();
        item.setId(3L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        booking = BookingMapper.toBookingFromCreateDto(bookingCreateDto, item, booker);
        booking.setId(4L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
    }

    @Test
    void getBookingByIdWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(booker.getId(), booking.getId()));

        assertEquals("Бронирование " + booking.getId() + " не найдено.", exception.getMessage());
    }

    @Test
    void getBookingByIdWhenUserIsNotBookerOrOwner() {
        User anotherUser = new User();
        anotherUser.setId(999L);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(anotherUser.getId(), booking.getId()));

        assertEquals("Просматривать бронирование может только владелец вещи или создатель брони.", exception.getMessage());
    }

    @Test
    void createBookingReturnBookingDtoWhenDataIsValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.create(booker.getId(), bookingCreateDto);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStatus(), result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingWhenUserIsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.create(999L, bookingCreateDto));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void createBookingWhenItemIsNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.create(booker.getId(), bookingCreateDto));

        assertEquals("Вещь не найдена.", exception.getMessage());
    }

    @Test
    void createBookingWhenTimeIsUnavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                bookingService.create(booker.getId(), bookingCreateDto));

        assertEquals("Данное время уже занято для бронирования.", exception.getMessage());
    }

    @Test
    void createBookingWhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findOverlappingBookings(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                bookingService.create(booker.getId(), bookingCreateDto));

        assertEquals("Вещь не доступна для бронирования.", exception.getMessage());
    }

    @Test
    void createBookingWhenStartIsNotBeforeEnd() {
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                bookingService.create(booker.getId(), bookingCreateDto));

        assertEquals("Время начала должно быть раньше времени окончания.", exception.getMessage());
    }

    @Test
    void updateBookingReturnUpdatedBookingDtoWhenApprovedIsTrue() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.update(owner.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBookingReturnUpdatedBookingDtoWhenApprovedIsFalse() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        var result = bookingService.update(owner.getId(), booking.getId(), false);

        assertNotNull(result);
        assertEquals(Status.REJECTED, result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBookingWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.update(owner.getId(), booking.getId(), true));

        assertEquals("Бронирование " + booking.getId() + " не найдено.", exception.getMessage());
    }

    @Test
    void updateBookingWhenUserIsNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(999L);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.update(anotherUser.getId(), booking.getId(), true));

        assertEquals("Только владелец вещи может подтвердить или отклонить бронирование.", exception.getMessage());
    }

    @Test
    void updateBookingWhenStatusIsNotWaiting() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                bookingService.update(owner.getId(), booking.getId(), true));

        assertEquals("Статус можно изменить только у бронирования со статусом WAITING.", exception.getMessage());
    }

    @Test
    void getBookingByIdWhenUserIsBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        var result = bookingService.getBookingById(booker.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingByIdWhenUserIsOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        var result = bookingService.getBookingById(owner.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }


    @Test
    void findAllReturnListOfBookingDtoWhenStateIsAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "ALL", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllReturnListOfBookingDtoWhenStateIsCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "CURRENT", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllReturnListOfBookingDtoWhenStateIsPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "PAST", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllReturnListOfBookingDtoWhenStateIsFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "FUTURE", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllReturnListOfBookingDtoWhenStateIsWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusAndStartAfterOrderByStartDesc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "WAITING", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllReturnListOfBookingDtoWhenStateIsRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class))).thenReturn(List.of(booking));

        var result = bookingService.findAll(booker.getId(), "REJECTED", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.findAll(999L, "ALL", 0, 10));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "ALL", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsCurrent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "CURRENT", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsPast() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "PAST", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsFuture() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class))).thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "FUTURE", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsWaiting() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "WAITING", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsReturnListOfBookingDtoWhenStateIsRejected() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class))).thenReturn(List.of(booking));

        var result = bookingService.getOwnerBookings(owner.getId(), "REJECTED", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookingsWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerBookings(999L, "ALL", 0, 10));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}