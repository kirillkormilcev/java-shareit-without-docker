package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final UserDao userDao;

    public UserDto addUserToStorage(UserDto userDto) {
        checkEmail(userDto);
        return UserMapper.toUserDto(userDao.save(UserMapper.toUser(userDto)));
    }

    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userDao.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе.")));
    }

    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto updateUser(UserDto userDto, long userId) {
        if (userDto == null) {
            throw new IncorrectRequestParamException("На обновление поступил null пользователь.");
        }
        updateNotNullField(userDto, getUserById(userId));
        userDto.setId(userId);
        return UserMapper.toUserDto(userDao.save(UserMapper.toUser(userDto)));
    }

    public void deleteUserById(long userId) {
        getUserById(userId);
        userDao.deleteById(userId);
    }

    private void updateNotNullField(UserDto userDto, UserDto userDtoFromDao) {
        if (userDto.getName() == null) {
            userDto.setName(userDtoFromDao.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userDtoFromDao.getEmail());
        }
    }

    private void checkEmail(UserDto userDto) {
        if (userDao.getAllEmails().contains(userDto.getEmail())) {
            throw new UserValidationException("Пользователь с почтой " + userDto.getEmail() + " уже зарегистрирован.");
        }
    }
}
