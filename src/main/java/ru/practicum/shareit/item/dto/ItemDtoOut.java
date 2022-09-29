package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoOutForItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDtoOut {
    long id;
    String name;
    String description;
    Boolean available;
    //Request request; // todo возможно понадобится в будущем
    BookingDtoOutForItem lastBooking;
    BookingDtoOutForItem nextBooking;
    List<CommentDto> comments;
}
