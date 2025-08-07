package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public User create(User user) {
        log.info("Creating user: {}", user.getEmail());
        validateEmail(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created user: e mail {} and id {}", user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User update(User updateUser) {
        log.info("Updating user: {}", updateUser.getId());
        if (users.containsKey(updateUser.getId())) {
            User oldInformationUser = users.get(updateUser.getId());

            Optional.ofNullable(updateUser.getName()).ifPresent(oldInformationUser::setName);
            if (updateUser.getEmail() != null && !oldInformationUser.getEmail().equals(updateUser.getEmail())) {
                validateEmail(updateUser);
                oldInformationUser.setEmail(updateUser.getEmail());
            }

            log.info("Пользователь с id {} успешно обновлен", updateUser.getId());
            return oldInformationUser;

        } else {
            log.warn("Пользователь с id {} не найден для обновления", updateUser.getId());
            throw new NotFoundException("Пользователь с id " + updateUser.getId() + " не найден");
        }
    }

    @Override
    public Collection<User> findAll() {
        log.info("Список всех пользователей");
        return users.values();
    }

    @Override
    public User findUserById(Long id) {
        log.info("Поиск пользователя по ID: {}", id);
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        log.info("Удаление пользователя по ID: {}", id);
        users.remove(id);
    }

    private void validateEmail(User user) throws DuplicatedDataException {
        if (user.getEmail() != null && users.values().stream()
                .anyMatch(u -> u.getEmail() != null && u.getEmail()
                        .equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            log.error("Email {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот email уже используется");
        }
    }

    private long getNextId() {
        long maxId = users.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        return counter.updateAndGet(current -> Math.max(current, maxId + 1));
    }
}
