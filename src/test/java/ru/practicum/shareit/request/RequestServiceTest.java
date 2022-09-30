package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.PageRequestModified;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDtoIn;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RequestServiceTest {
    final RequestRepository requestRepository = mock(RequestRepository.class);
    final UserRepository userRepository = mock(UserRepository.class);
    final ItemRepository itemRepository = mock(ItemRepository.class);

    RequestService requestService = new RequestService(requestRepository, userRepository, itemRepository);

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.ru")
            .build();

    Request request1 = Request.builder()
            .id(1L)
            .description("r_desc")
            .requestor(user1)
            .build();

    RequestDtoIn requestDtoIn1 = RequestDtoIn.builder()
            .id(1L)
            .description("r_desc")
            .build();

    @Test
    void testRequestService() {

        Mockito.when(requestRepository.save(Mockito.any(Request.class))).thenReturn(request1);

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(user1));

        assertEquals(requestService.addRequest(requestDtoIn1, user1.getId()).getId(), request1.getId());

        Mockito.when(requestRepository.findByRequestorId(user1.getId(), Sort.by("created").descending()))
                .thenReturn(List.of(request1));

        assertEquals(requestService.getRequestsByUserId(user1.getId()).get(0).getId(), request1.getId());

        Mockito.when(requestRepository.findRequestOfOtherUsers(user1.getId(), new PageRequestModified(0, 10, Sort.by("created").descending())))
                .thenReturn(List.of(request1));

        assertEquals(requestService.getRequestsOfOtherUsers(user1.getId(), 0, 10).get(0).getId(), request1.getId());

        Mockito.when(requestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(request1));

        assertEquals(requestService.getRequestById(user1.getId(), request1.getId()).getId(), request1.getId());

    }
}