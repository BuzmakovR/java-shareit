package ru.practicum.shareit.item.dao;


import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

	Item get(Long id);

	Item add(Item item);

	Item update(Item updateItem);

	Item delete(Long id);

	Collection<Item> getByUserId(Long userId);

	Collection<Item> search(String name);
}
