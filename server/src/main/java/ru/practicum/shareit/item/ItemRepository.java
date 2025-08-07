package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner);

    @Query(value = "SELECT i " +
            "FROM Item as i " +
            "WHERE i.available = true and " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%') ) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%') ))")
    List<Item> searchItems(String text);

    @Query("SELECT DISTINCT i FROM Item i " +
            "JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.comments " +
            "WHERE i.owner = :owner")
    List<Item> findAllByOwnerWithComments(@Param("owner") User owner);

    List<Item> findByRequestId(Long requestId);
}
