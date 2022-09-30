package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class UserService {
    final UserRepository userRepository;

    @Transactional
    public UserDto addUserToStorage(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе.")));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, long userId) {
        if (userDto == null) {
            throw new IncorrectRequestParamException("На обновление поступил null пользователь.");
        }
        User userFromDao = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе."));
        UserMapper.updateNotNullField(UserMapper.toUser(userDto), userFromDao);
        return UserMapper.toUserDto(userRepository.save(userFromDao));
    }

    @Transactional
    public void deleteUserById(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }
}
