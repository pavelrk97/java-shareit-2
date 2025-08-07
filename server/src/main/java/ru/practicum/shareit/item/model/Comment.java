package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "text", nullable = false)
    String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    Item item;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    User author;

    @Column(name = "created")
    @CreationTimestamp
    LocalDateTime created;
}