package ru.practicum.shareit.booking.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.annotation.IsEndAfterStart;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.error.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@IsEndAfterStart(groups = {Create.class})
public class BookingDtoIn {
    @NotNull(groups = {Create.class})
    @FutureOrPresent(groups = {Create.class})
    LocalDateTime start;
    @FutureOrPresent(groups = {Create.class})
    @JsonProperty("end")
    LocalDateTime ending;
    Long itemId;
    @Builder.Default
    Status status = Status.WAITING;
}
