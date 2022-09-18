package ru.practicum.shareit.booking.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    long id;
    LocalDateTime start;
    LocalDateTime ending;
    Item item;
    User booker;
    Status status;
}
