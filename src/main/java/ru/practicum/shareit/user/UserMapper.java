package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static void updateNotNullField(User user, User userFromRepo) {
        if (user.getName() != null) {
            userFromRepo.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromRepo.setEmail(user.getEmail());
        }
    }
}
