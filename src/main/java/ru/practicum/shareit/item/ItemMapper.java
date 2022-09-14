package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequest(item.getItemRequest() != null ? item.getItemRequest() : null)
                .reviews(item.getReviews() != null ? item.getReviews() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner() != null ? itemDto.getOwner() : null)
                .itemRequest(itemDto.getItemRequest() != null ? itemDto.getItemRequest() : null)
                .reviews(itemDto.getReviews() != null ? itemDto.getReviews() : null)
                .build();
    }
}