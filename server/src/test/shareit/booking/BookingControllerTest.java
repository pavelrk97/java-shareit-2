package shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.utils.HeaderConstants;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);


        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
    }

    @Test
    void createBookingReturnCreatedBooking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingCreateDto.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).create(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void updateBookingReturnUpdatedBooking() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).update(anyLong(), anyLong(), any(Boolean.class));
    }

    @Test
    void findBookingByIdReturnBookingDto() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }

    @Test
    void findAllReturnListOfBookings() throws Exception {
        when(bookingService.findAll(anyLong(), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(get("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).findAll(anyLong(), any(String.class), any(Integer.class), any(Integer.class));
    }

    @Test
    void getAllOwnerReturnListOfBookings() throws Exception {
        when(bookingService.getOwnerBookings(anyLong(), any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class));

        verify(bookingService, times(1)).getOwnerBookings(anyLong(), any(String.class), any(Integer.class), any(Integer.class));
    }

    @Test
    void createBookingWhenServiceThrowsNotFoundException() throws Exception {
        when(bookingService.create(anyLong(), any(BookingCreateDto.class)))
                .thenThrow(new NotFoundException("Вещь не найдена"));

        mockMvc.perform(post("/bookings")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Ожидаем код 404
        verify(bookingService, times(1)).create(anyLong(), any(BookingCreateDto.class));
    }

    @Test
    void updateBookingWhenServiceThrowsNotFoundException() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), any(Boolean.class)))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Ожидаем код 404

        verify(bookingService, times(1)).update(anyLong(), anyLong(), any(Boolean.class));
    }

    @Test
    void findBookingByIdWhenServiceThrowsNotFoundException() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Бронирование не найдено"));

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Ожидаем код 404
        verify(bookingService, times(1)).getBookingById(anyLong(), anyLong());
    }
}