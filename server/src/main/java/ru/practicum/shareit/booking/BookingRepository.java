package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //ALL state by bookerId
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    //CURRENT state by bookingId
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime end, LocalDateTime start);

    //PAST state by bookingId
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    //FUTURE state by bookingId
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    //WAITING state by bookingId
    List<Booking> findByBookerIdAndStatusAndStartAfterOrderByStartDesc(Long bookerId, Status status, LocalDateTime start);

    //REJECT state by bookingId
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    //ALL state by ownerId
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    //CURRENT state by ownerId
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime end, LocalDateTime start);

    //PAST state by ownerId
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    //FUTURE state by ownerId
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    //WAITING state by ownerId
    List<Booking> findByItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(Long ownerId, Status status, LocalDateTime start);

    //REJECT state by ownerId
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_date < ?2 " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getLastBooking(Long idItem, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.item_id = ?1 AND b.start_date > ?2 AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC LIMIT 1 ", nativeQuery = true)
    Optional<Booking> getNextBooking(Long idItem, LocalDateTime currentTime);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 AND i.id = ?2 AND b.status = 'APPROVED' AND b.end_date < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.start < ?3 " +
            "AND b.end > ?2")
    List<Booking> findOverlappingBookings(Long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, Status status);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, Status bookingStatus);
}