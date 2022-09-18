package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.review.model.Review;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.error.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemDto {
    long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    String description;
    @NotNull(groups = {Create.class})
    Boolean available;
    User owner;
    ItemRequest itemRequest;
    List<Review> reviews;
}
