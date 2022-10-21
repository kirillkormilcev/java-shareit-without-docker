package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.PageRequestModified;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.dto.RequestDtoOut;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class RequestService {
    final RequestRepository requestRepository;
    final UserRepository userRepository;
    final ItemRepository itemRepository;

    @Transactional
    public RequestDtoOut addRequest(RequestDtoIn requestDtoIn, long userId) {
        Request request = RequestMapper.toRequest(requestDtoIn);
        request.setRequestor(findUserById(userId));
        return RequestMapper.toRequestDtoOut(requestRepository.save(request));
    }

    public List<RequestDtoOut> getRequestsByUserId(long userId) {
        findUserById(userId);
        Sort sort = Sort.by("created").descending();
        return requestRepository.findByRequestorId(userId, sort).stream().map(r -> addParamsToRequest(r, userId))
                .collect(Collectors.toList());
    }

    public List<RequestDtoOut> getRequestsOfOtherUsers(long userId, Integer from, Integer size) {
        final PageRequest pageRequest = new PageRequestModified(from, size, Sort.by("created").descending());
        return requestRepository.findRequestOfOtherUsers(userId, pageRequest).stream().map(r -> addParamsToRequest(r, userId))
                .collect(Collectors.toList());
    }

    public RequestDtoOut getRequestById(long userId, long requestId) {
        findUserById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с индексом " + requestId + " не найден в базе."));
        return addParamsToRequest(request, userId);
    }

    private RequestDtoOut addParamsToRequest(Request request, long userId) {
        RequestDtoOut requestDtoOut = RequestMapper.toRequestDtoOut(request);
        requestDtoOut.setItems(itemRepository.findByRequestId(requestDtoOut.getId()).stream()
                .map(ItemMapper::toItemDtoOut).collect(Collectors.toList()));
        return requestDtoOut;
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе."));
    }
}
