package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {

	ItemDto getItem(Long id);

	ItemDto addItem(CreateItemRequest createItemRequest, Long ownerId);

	ItemDto updateItem(Long itemId, UpdateItemRequest item, Long ownerId);

	void deleteItem(Long id, Long ownerId);

	Collection<ItemOwnerDto> getItemsByUser(Long userId);

	Collection<ItemDto> search(String name);

	CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
