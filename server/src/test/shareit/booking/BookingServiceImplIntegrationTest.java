package shareit.booking;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceImplIntegrationTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Test User");
        booker.setEmail("test@yandex.ru");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(booker);
        this.item = itemRepository.save(item);
    }

    @Test
    void createBookingSaveBookingToDatabase() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.create(booker.getId(), bookingCreateDto);
        Optional<Booking> retrievedBooking = bookingRepository.findById(item.getId());

        assertTrue(retrievedBooking.isPresent(), "Бронирование должно быть сохранено в базе данных");
        assertEquals(Status.WAITING, retrievedBooking.get().getStatus(), "Статус бронирования должен быть WAITING");
    }

    @Test
    void updateBookingStatus() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(booker.getId(), bookingCreateDto);

        BookingDto updatedBooking = bookingService.update(booker.getId(), createdBooking.getId(), true);

        Optional<Booking> retrievedBooking = bookingRepository.findById(updatedBooking.getId());
        assertTrue(retrievedBooking.isPresent());
        assertEquals(Status.APPROVED, retrievedBooking.get().getStatus());
    }

    @Test
    void getBookingById() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto createdBooking = bookingService.create(booker.getId(), bookingCreateDto);

        BookingDto retrievedBooking = bookingService.getBookingById(booker.getId(), createdBooking.getId());

        assertEquals(createdBooking.getId(), retrievedBooking.getId());
    }

    @Test
    void findAllBookingsForBooker() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(item.getId());
        bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(booker.getId(), bookingCreateDto);

        List<BookingDto> bookings = bookingService.findAll(booker.getId(), "ALL", 0, 10);

        assertEquals(1, bookings.size());
    }
}