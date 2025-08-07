package shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.HeaderConstants;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private CommentDto commentDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() throws Exception {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Test Item");
        itemCreateDto.setDescription("Test Description");
        itemCreateDto.setAvailable(true);

        itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("Updated Item");
        itemUpdateDto.setDescription("Updated Description");
        itemUpdateDto.setAvailable(false);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment");

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("Test Comment");
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.create(anyLong(), any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(itemService, times(1)).create(anyLong(), any(ItemCreateDto.class));
    }

    @Test
    void updateItemTest_NoUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemDtoById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(itemService, times(1)).getItemDtoById(anyLong(), anyLong());
    }

    @Test
    void getItemByIdTest_NoUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/{itemId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByUserIdTest() throws Exception {
        when(itemService.getAllItemDtoByUserId(anyLong())).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items")
                        .header(HeaderConstants.USER_ID_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(itemService, times(1)).getAllItemDtoByUserId(anyLong());
    }

    @Test
    void getItemsByUserIdTest_NoUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyLong(), any(String.class))).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(itemService, times(1)).searchItems(anyLong(), any(String.class));
    }

    @Test
    void searchItemsTest_NoUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemsTestEmptyTextReturnsOkAndEmptyList() throws Exception {
        when(itemService.searchItems(anyLong(), any(String.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).searchItems(anyLong(), any(String.class));
    }

    @Test
    void searchItemsTestBlankTextReturnsOkAndEmptyList() throws Exception {
        when(itemService.searchItems(anyLong(), any(String.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .param("text", "   "))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService, times(1)).searchItems(anyLong(), any(String.class));
    }

    @Test
    void deleteItemTest() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(1L);
    }


    @Test
    void createCommentTest() throws Exception {
        when(itemService.createComment(anyLong(), any(CommentCreateDto.class), anyLong())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(HeaderConstants.USER_ID_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
        verify(itemService, times(1)).createComment(anyLong(), any(CommentCreateDto.class), anyLong());
    }

    @Test
    void createCommentTest_NoUserIdHeader_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }
}