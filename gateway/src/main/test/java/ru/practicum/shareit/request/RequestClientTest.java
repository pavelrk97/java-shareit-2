package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestClientTest {

    @Mock
    private RequestClient requestClient;

    @Test
    void createRequest() {
        Long userId = 1L;
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("description");
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Запрос создан", HttpStatus.OK);

        when(requestClient.create(userId, requestDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = requestClient.create(userId, requestDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findUserRequests() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Запросы пользователя", HttpStatus.OK);

        when(requestClient.findUserRequests(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = requestClient.findUserRequests(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Все запросы", HttpStatus.OK);

        when(requestClient.findAllRequests(userId, from, size)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = requestClient.findAllRequests(userId, from, size);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findRequestById() {
        Long requestId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Запрос по Id", HttpStatus.OK);

        when(requestClient.findRequestById(requestId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = requestClient.findRequestById(requestId);
        assertEquals(expectedResponse, actualResponse);
    }
}