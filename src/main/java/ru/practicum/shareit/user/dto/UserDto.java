package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.error.validation.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    long id;
    @NotBlank(groups = {Create.class})
    String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Create.class}, message = "Не верный формат email.")
    String email;
}
