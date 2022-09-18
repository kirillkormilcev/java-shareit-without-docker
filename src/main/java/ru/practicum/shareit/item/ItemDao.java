package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findItemByOwnerId(long userId);

    List<Item> findByNameOrDescriptionIgnoreCaseContainingAndAvailableIsTrue(String name, String description);
}
