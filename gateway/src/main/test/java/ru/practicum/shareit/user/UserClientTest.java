package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private UserClient userClient;

    @Test
    void createUser() {
        UserRequestDto userDto = UserRequestDto.builder().name("Test User").email("test@yandex.ru").build();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Пользователь создан", HttpStatus.OK);

        when(userClient.create(userDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.create(userDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findUserById() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Пользователь найден", HttpStatus.OK);

        when(userClient.findById(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.findById(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void updateUser() {
        Long userId = 1L;
        UserRequestDto userDto = UserRequestDto.builder().name("Updated User").email("updated@yandex.ru").build();
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Данные пользователя обновлены", HttpStatus.OK);

        when(userClient.update(userId, userDto)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.update(userId, userDto);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void deleteUserById() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Пользователь удален", HttpStatus.OK);

        when(userClient.deleteById(userId)).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.deleteById(userId);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void findAllUsers() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Все пользователи", HttpStatus.OK);

        when(userClient.findAll()).thenReturn(expectedResponse);

        ResponseEntity<Object> actualResponse = userClient.findAll();
        assertEquals(expectedResponse, actualResponse);
    }
}