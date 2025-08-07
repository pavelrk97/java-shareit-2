package shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentCreateDtoTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Test
    void testCommentCreateDtoSerialization() throws IOException {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Test comment");
        var jsonOutput = json.write(commentCreateDto);
        assertThat(jsonOutput).hasJsonPath("$.text");
        assertThat(jsonOutput).extractingJsonPathStringValue("$.text").isEqualTo("Test comment");
    }

    @Test
    void testCommentCreateDtoDeserialization() throws IOException {
        String jsonContent = "{\"text\":\"Test comment\"}";
        CommentCreateDto commentCreateDto = json.parse(jsonContent).getObject();
        assertThat(commentCreateDto.getText()).isEqualTo("Test comment");
    }

    @Test
    void testCommentCreateDtoSerialization_emptyText() throws IOException {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("");
        var jsonOutput = json.write(commentCreateDto);
        assertThat(jsonOutput).hasJsonPath("$.text");
        assertThat(jsonOutput).extractingJsonPathStringValue("$.text").isEqualTo("");
    }

    @Test
    void testCommentCreateDtoDeserialization_emptyText() throws IOException {
        String jsonContent = "{\"text\":\"\"}";
        CommentCreateDto commentCreateDto = json.parse(jsonContent).getObject();
        assertThat(commentCreateDto.getText()).isEqualTo("");
    }

    @Test
    void testCommentCreateDtoSerialization_nullText() throws IOException {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText(null);
        var jsonOutput = json.write(commentCreateDto);
        assertThat(jsonOutput).hasJsonPath("$.text");
        assertThat(jsonOutput.getJson()).contains("\"text\":null");
    }

    @Test
    void testCommentCreateDtoDeserialization_nullText() throws IOException {
        String jsonContent = "{\"text\":null}";
        CommentCreateDto commentCreateDto = json.parse(jsonContent).getObject();
        assertThat(commentCreateDto.getText()).isNull();
    }
}