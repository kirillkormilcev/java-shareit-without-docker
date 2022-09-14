package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.IncorrectRequestParamException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemService {
    final ItemDao itemDao;
    final UserDao userDao;

    public ItemDto addItemToStorage(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userDao.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с индексом " + userId + " не найден в базе.")));
        return ItemMapper.toItemDto(itemDao.save(item));
    }

    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        if (itemDto == null) {
            throw new IncorrectRequestParamException("На обновление поступила null вещь.");
        }
        Item itemFromDao = itemDao.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с индексом " + itemId + " не найдена в базе."));
        if (userId != itemFromDao.getOwner().getId()) {
            throw new NotFoundException("Редактировать вещь имеет право только хозяин.");
        }
        updateNotNullField(itemDto, ItemMapper.toItemDto(itemFromDao));
        itemDto.setOwner(itemFromDao.getOwner());
        itemDto.setId(itemId);
        return ItemMapper.toItemDto(itemDao.save(ItemMapper.toItem(itemDto)));
    }

    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemDao.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с индексом " + itemId + " не найдена в базе.")));
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        return itemDao.findItemByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchAvailableItemsByPartOfName(String text, long userId) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemDao.findByNameOrDescriptionIgnoreCaseContainingAndAvailableIsTrue(text, text).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void updateNotNullField(ItemDto itemDto, ItemDto itemDtoFromDao) {
        if (itemDto.getName() == null) {
            itemDto.setName(itemDtoFromDao.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemDtoFromDao.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemDtoFromDao.getAvailable());
        }
    }
}
