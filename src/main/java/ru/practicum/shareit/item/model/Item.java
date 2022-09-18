package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.review.model.Review;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "items")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    Boolean available;
    @OneToOne(cascade = CascadeType.ALL)
    User owner;
    @OneToOne(cascade = CascadeType.ALL)
    ItemRequest itemRequest;
    @OneToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    List<Review> reviews;
}
