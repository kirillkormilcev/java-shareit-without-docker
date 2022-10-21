package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserMapper;

public class RequestMapper {
    public static RequestDtoOut toRequestDtoOut(Request request) {
        return RequestDtoOut.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor() != null ? UserMapper.toUserDto(request.getRequestor()) : null)
                .created(request.getCreated())
                .build();
    }

    public static Request toRequest(RequestDtoIn requestDtoIn) {
        return Request.builder()
                .id(requestDtoIn.getId())
                .description(requestDtoIn.getDescription())
                .created(requestDtoIn.getCreated())
                .build();
    }
}
