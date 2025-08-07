package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    @ToString.Exclude
    User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;
}
