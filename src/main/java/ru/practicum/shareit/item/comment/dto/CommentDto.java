package ru.practicum.shareit.item.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.error.validation.Create;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    long id;
    @NotBlank(groups = {Create.class})
    String text;
    ItemDtoOut item;
    String authorName;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
}
