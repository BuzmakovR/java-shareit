package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;

	private final UserRepository userRepository;

	@Override
	public ItemDto getItem(Long id) {
		return ItemMapper.toItemDto(itemRepository.get(id));
	}

	@Override
	public ItemDto addItem(ItemDto itemDto, Long ownerId) {
		userRepository.get(ownerId);
		Item item = Item.builder()
				.ownerId(ownerId)
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.isAvailable(itemDto.getIsAvailable())
				.request(itemDto.getRequest())
				.build();
		return ItemMapper.toItemDto(itemRepository.add(item));
	}

	@Override
	public ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long userId) {
		Item item = itemRepository.get(itemId);
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
		return ItemMapper.toItemDto(itemRepository.update(item));
	}

	@Override
	public void deleteItem(Long id, Long userId) {
		itemRepository.delete(id);
	}

	@Override
	public Collection<ItemDto> getItemsByUser(Long userId) {
		userRepository.get(userId);
		return itemRepository.getByUserId(userId).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public Collection<ItemDto> search(String name) {
		if (name == null || name.isBlank()) {
			return List.of();
		}
		return itemRepository.search(name).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
