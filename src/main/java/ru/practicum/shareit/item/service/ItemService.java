package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {

	ItemDto getItem(Long id);

	ItemDto addItem(ItemDto item, Long ownerId);

	ItemDto updateItem(Long itemId, UpdateItemRequest item, Long ownerId);

	void deleteItem(Long id, Long ownerId);

	Collection<ItemDto> getItemsByUser(Long userId);

	Collection<ItemDto> search(String name);

}
