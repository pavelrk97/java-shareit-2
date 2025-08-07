package shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
public class BookingCreateDtoTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void testSerialize() throws IOException {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .bookerId(1L)
                .status(Status.WAITING)
                .build();

        var jsonContent = json.write(bookingCreateDto);

        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).hasJsonPath("$.bookerId");
        assertThat(jsonContent).hasJsonPath("$.status");
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonString = "{\"start\":\"2024-10-27T10:00:00\",\"end\":\"2024-10-28T10:00:00\",\"itemId\":1,\"bookerId\":1,\"status\":\"WAITING\"}";
        BookingCreateDto bookingCreateDto = json.parseObject(jsonString);

        assertThat(bookingCreateDto).isNotNull();
        assertThat(bookingCreateDto.getItemId()).isEqualTo(1L);
        assertThat(bookingCreateDto.getBookerId()).isEqualTo(1L);
        assertThat(bookingCreateDto.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void testDeserializeWithNullStatus() throws IOException {
        String jsonString = "{\"start\":\"2024-10-27T10:00:00\",\"end\":\"2024-10-28T10:00:00\",\"itemId\":1,\"bookerId\":1,\"status\":null}";
        BookingCreateDto bookingCreateDto = json.parseObject(jsonString);

        assertThat(bookingCreateDto).isNotNull();
        assertNull(bookingCreateDto.getStatus());
    }

    @Test
    void testDeserializeWithMissingStatus() throws IOException {
        String jsonString = "{\"start\":\"2024-10-27T10:00:00\",\"end\":\"2024-10-28T10:00:00\",\"itemId\":1,\"bookerId\":1}";
        BookingCreateDto bookingCreateDto = json.parseObject(jsonString);

        assertThat(bookingCreateDto).isNotNull();
        assertNull(bookingCreateDto.getStatus());
    }

    @Test
    void testDeserializeWithMissingFields() throws IOException {
        String jsonString = "{\"itemId\":1}";
        BookingCreateDto bookingCreateDto = json.parseObject(jsonString);

        assertThat(bookingCreateDto).isNotNull();
        assertThat(bookingCreateDto.getItemId()).isEqualTo(1L);
    }

    @Test
    void testSerializeWithNullValues() throws IOException {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder().build();
        var jsonContent = json.write(bookingCreateDto);
        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).hasJsonPath("$.bookerId");
        assertThat(jsonContent).hasJsonPath("$.status");
    }

    @Test
    void testSerializeWithNullStatus() throws IOException {
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .itemId(1L)
                .bookerId(1L)
                .status(null)
                .build();

        var jsonContent = json.write(bookingCreateDto);

        assertThat(jsonContent).hasJsonPath("$.start");
        assertThat(jsonContent).hasJsonPath("$.end");
        assertThat(jsonContent).hasJsonPath("$.itemId");
        assertThat(jsonContent).hasJsonPath("$.bookerId");
        assertThat(jsonContent).hasJsonPath("$.status");

        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathValue("$.status").isNull();
    }
}