package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDtoOutForItem;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDtoOut toItemDtoOut(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                //.request(item.getRequest() != null ? item.getRequest() : null)
                // todo возможно понадобится в будущем
                .build();
    }

    public static Item toItem(ItemDtoIn itemDtoIn) {
        return Item.builder()
                .id(itemDtoIn.getId())
                .name(itemDtoIn.getName())
                .description(itemDtoIn.getDescription())
                .available(itemDtoIn.getAvailable())
                //.request(itemDtoIn.getRequest() != null ? itemDtoIn.getRequest() : null)
                // todo возможно понадобится в будущем
                .build();
    }

    public static void setOwner(Item item, User owner) {
        item.setOwner(owner);
    }

    public static void setLastBooking(ItemDtoOut itemDtoOut, BookingDtoOutForItem lastBooking) {
        itemDtoOut.setLastBooking(lastBooking);
    }

    public static void setNextBooking(ItemDtoOut itemDtoOut, BookingDtoOutForItem nextBooking) {
        itemDtoOut.setNextBooking(nextBooking);
    }

    public static void setComments(ItemDtoOut itemDtoOut, List<CommentDto> comments) {
        itemDtoOut.setComments(comments);
    }

    public static void updateNotNullField(Item item, Item itemFromRepo) {
        if (item.getName() != null) {
            itemFromRepo.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemFromRepo.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromRepo.setAvailable(item.getAvailable());
        }
    }
}
