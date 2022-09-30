package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.error.validation.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RequestDtoIn {
    long id;
    @NotBlank(groups = {Create.class})
    String description;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
}
