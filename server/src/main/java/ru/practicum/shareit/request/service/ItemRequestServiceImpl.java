package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	private static final String NOT_FOUND_ITEM_REQUEST_BY_ID = "Запрос не найден: ID = %d";
	private static final String NOT_FOUND_USER_BY_ID = "Пользователь не найден: ID = %d";

	private final ItemRequestRepository itemRequestRepository;

	private final ItemRepository itemRepository;

	private final UserRepository userRepository;

	@Override
	public ItemRequestDto getItemRequest(Long id) {
		Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(id);
		if (optionalItemRequest.isEmpty()) {
			throw new NotFoundException(NOT_FOUND_ITEM_REQUEST_BY_ID, id);
		}
		ItemRequest itemRequest = optionalItemRequest.get();
		return ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestIdIn(Set.of(id)));
	}

	@Override
	public Collection<ItemRequestDto> getItemRequests(Long userId) {
		Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
		Set<Long> requestIds = itemRequests.stream()
				.map(ItemRequest::getId)
				.collect(Collectors.toSet());
		Map<Long, List<Item>> itemsByRequestIds = getItemsByRequestIds(requestIds);
		return itemRequests.stream()
				.map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, itemsByRequestIds.getOrDefault(itemRequest.getId(), List.of())))
				.toList();

	}

	@Override
	public Collection<ItemRequestDto> getItemRequestsFromOtherUsers(Long userId) {
		Collection<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
		Set<Long> requestIds = itemRequests.stream()
				.map(ItemRequest::getId)
				.collect(Collectors.toSet());
		Map<Long, List<Item>> itemsByRequestIds = getItemsByRequestIds(requestIds);
		return itemRequests.stream()
				.map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, itemsByRequestIds.getOrDefault(itemRequest.getId(), List.of())))
				.toList();
	}

	@Override
	public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long ownerId) {
		Optional<User> optionalUser = userRepository.findById(ownerId);
		User owner = optionalUser.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, ownerId));

		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, owner);
		itemRequest.setCreated(LocalDateTime.now());
		return ItemRequestMapper.toItemRequestDto(itemRequestRepository.saveAndFlush(itemRequest));
	}

	private Map<Long, List<Item>> getItemsByRequestIds(Set<Long> requestIds) {
		Map<Long, List<Item>> itemsByRequestIds = new HashMap<>();
		itemRepository.findAllByRequestIdIn(requestIds)
				.forEach(item -> {
					List<Item> itemsByRequestId = itemsByRequestIds.getOrDefault(item.getRequest().getId(), new ArrayList<>());
					itemsByRequestId.add(item);
					itemsByRequestIds.put(item.getRequest().getId(), itemsByRequestId);
				});
		return itemsByRequestIds;
	}
}
