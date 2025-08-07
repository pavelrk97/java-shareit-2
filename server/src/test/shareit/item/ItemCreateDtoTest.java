package shareit.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
public class ItemCreateDtoTest {

    @Autowired
    private ObjectMapper json;

    @Test
    void testItemCreateDtoSerialization() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        String jsonString = json.writeValueAsString(itemCreateDto);

        assertThat(jsonString).contains("\"name\":\"Test Item\"");
        assertThat(jsonString).contains("\"description\":\"Test Description\"");
        assertThat(jsonString).contains("\"available\":true");
        assertThat(jsonString).contains("\"requestId\":1");
    }

    @Test
    void testItemCreateDtoDeserialization() throws IOException {
        String jsonString = "{\"name\":\"Test Item\", \"description\":\"Test Description\", \"available\":true, \"requestId\":1}";

        ItemCreateDto itemCreateDto = json.readValue(jsonString, ItemCreateDto.class);

        assertThat(itemCreateDto.getName()).isEqualTo("Test Item");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemCreateDto.getAvailable()).isEqualTo(true);
        assertThat(itemCreateDto.getRequestId()).isEqualTo(1L);
    }

    @Test
    void testItemCreateDtoSerialization_nullValues() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().build();
        String jsonString = json.writeValueAsString(itemCreateDto);

        assertThat(jsonString).contains("\"name\":null");
        assertThat(jsonString).contains("\"description\":null");
        assertThat(jsonString).contains("\"available\":null");
        assertThat(jsonString).contains("\"requestId\":null");
    }

    @Test
    void testItemCreateDtoDeserialization_emptyJson() throws IOException {
        String jsonString = "{}";
        ItemCreateDto itemCreateDto = json.readValue(jsonString, ItemCreateDto.class);

        assertNull(itemCreateDto.getName());
        assertNull(itemCreateDto.getDescription());
        assertNull(itemCreateDto.getAvailable());
        assertNull(itemCreateDto.getRequestId());
    }

    @Test
    void testItemCreateDtoSerialization_emptyStrings() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("")
                .description("")
                .available(false)
                .requestId(0L)
                .build();

        String jsonString = json.writeValueAsString(itemCreateDto);

        assertThat(jsonString).contains("\"name\":\"\"");
        assertThat(jsonString).contains("\"description\":\"\"");
        assertThat(jsonString).contains("\"available\":false");
        assertThat(jsonString).contains("\"requestId\":0");
    }

    @Test
    void testItemCreateDtoSerialization_falseAvailable() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(false)
                .requestId(1L)
                .build();

        String jsonString = json.writeValueAsString(itemCreateDto);

        assertThat(jsonString).contains("\"available\":false");
    }

    @Test
    void testItemCreateDtoDeserialization_falseAvailable() throws IOException {
        String jsonString = "{\"name\":\"Test Item\", \"description\":\"Test Description\", \"available\":false, \"requestId\":1}";

        ItemCreateDto itemCreateDto = json.readValue(jsonString, ItemCreateDto.class);

        assertThat(itemCreateDto.getAvailable()).isEqualTo(false);
    }

    @Test
    void testItemCreateDtoSerialization_nullRequestId() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(null)
                .build();

        String jsonString = mapper.writeValueAsString(itemCreateDto);

        assertThat(jsonString).doesNotContain("\"requestId\":");
    }

    @Test
    void testItemCreateDtoDeserialization_nullRequestId() throws IOException {
        String jsonString = "{\"name\":\"Test Item\", \"description\":\"Test Description\", \"available\":true}"; //requestId отсутствует

        ItemCreateDto itemCreateDto = json.readValue(jsonString, ItemCreateDto.class);

        assertThat(itemCreateDto.getRequestId()).isNull();
        assertThat(itemCreateDto.getName()).isEqualTo("Test Item");
        assertThat(itemCreateDto.getDescription()).isEqualTo("Test Description");
        assertThat(itemCreateDto.getAvailable()).isEqualTo(true);
    }
}