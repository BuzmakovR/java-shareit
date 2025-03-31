package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemStorage itemStorage;

	private final UserStorage userStorage;

	@Override
	public ItemDto getItem(Long id) {
		return ItemMapper.toItemDto(itemStorage.get(id));
	}

	@Override
	public ItemDto addItem(Item item, Long ownerId) {
		userStorage.get(ownerId);
		item.setOwnerId(ownerId);
		return ItemMapper.toItemDto(itemStorage.add(item));
	}

	@Override
	public ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long userId) {
		Item item = itemStorage.get(itemId);
		if (!Objects.equals(item.getOwnerId(), userId)) {
			throw new NotFoundException("Не найден элемент для обновления для текущего пользователя");
		}
		if (itemRequest.getName() != null && !itemRequest.getName().isBlank()) {
			item.setName(itemRequest.getName());
		}
		if (itemRequest.getDescription() != null && !itemRequest.getDescription().isBlank()) {
			item.setDescription(itemRequest.getDescription());
		}
		if (itemRequest.getIsAvailable() != null) {
			item.setIsAvailable(itemRequest.getIsAvailable());
		}
		return ItemMapper.toItemDto(itemStorage.update(item));
	}

	@Override
	public ItemDto deleteItem(Long id, Long userId) {
		return ItemMapper.toItemDto(itemStorage.delete(id));
	}

	@Override
	public Collection<ItemDto> getItemsByUser(Long userId) {
		userStorage.get(userId);
		return itemStorage.getByUserId(userId).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public Collection<ItemDto> search(String name) {
		if (name == null || name.isBlank()) {
			return List.of();
		}
		return itemStorage.search(name).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
