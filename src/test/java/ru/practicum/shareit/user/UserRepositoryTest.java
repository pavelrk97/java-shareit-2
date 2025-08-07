package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
        user1 = User.builder().name("testingName").email("testing@yandex.ru").build();
        user2 = User.builder().name("testingName2").email("testingtwo@yandex.ru").build();
    }

    @Test
    void createUserTest() {
        User createdUser = userRepository.create(user1);
        assertNotNull(createdUser.getId());
        assertEquals(user1.getName(), createdUser.getName());
        assertEquals(user1.getEmail(), createdUser.getEmail());
    }

    @Test
    void emailIsDuplicatedTest() {
        userRepository.create(user1);
        User userWithSameEmail = User.builder().name("testingNewName").email("testing@yandex.ru").build();
        assertThrows(DuplicatedDataException.class, () -> userRepository.create(userWithSameEmail));
    }

    @Test
    void updateUserTest() {
        User createdUser = userRepository.create(user1);
        User userUpdate = User.builder().id(createdUser.getId()).name("newName").email("updatedTesting@yandex.ru").build();
        User updatedUser = userRepository.update(userUpdate);
        assertEquals("newName", updatedUser.getName());
        assertEquals("updatedTesting@yandex.ru", updatedUser.getEmail());
    }

    @Test
    void userNotFoundTest() {
        User userUpdate = User.builder().id(999L).name("newName").email("updatedTesting@yandex.ru").build();
        assertThrows(NotFoundException.class, () -> userRepository.update(userUpdate));
    }

    @Test
    void returnAllUsersTest() {
        userRepository.create(user1);
        userRepository.create(user2);
        Collection<User> allUsers = userRepository.findAll();
        assertEquals(2, allUsers.size());
    }

    @Test
    void returnUserWhenFoundTest() {
        User createdUser = userRepository.create(user1);
        User foundUser = userRepository.findUserById(createdUser.getId());
        assertEquals(createdUser.getId(), foundUser.getId());
    }

    @Test
    void returnNullWhenNotFoundTest() {
        User foundUser = userRepository.findUserById(999L);
        assertNull(foundUser);
    }

    @Test
    void deleteUserTest() {
        User createdUser = userRepository.create(user1);
        userRepository.delete(createdUser.getId());
        assertNull(userRepository.findUserById(createdUser.getId()));
    }
}
