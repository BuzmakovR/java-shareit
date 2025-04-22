package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

	private static final String NOT_FOUND_ITEM_REQUEST_BY_ID = "Запрос не найден: ID = %d";
	private static final String NOT_FOUND_USER_BY_ID = "Пользователь не найден: ID = %d";

	private final ItemRequestRepository itemRequestRepository;

	private final UserRepository userRepository;

	@Override
	public ItemRequestDto getItemRequest(Long id) {
		Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(id);
		if (optionalItemRequest.isEmpty()) {
			throw new NotFoundException(NOT_FOUND_ITEM_REQUEST_BY_ID, id);
		}
		return ItemRequestMapper.toItemRequestDto(optionalItemRequest.get());
	}

	@Override
	public Collection<ItemRequestDto> getItemRequests(Long userId) {
		return itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
				.stream()
				.map(ItemRequestMapper::toItemRequestDto)
				.toList();
	}

	@Override
	public Collection<ItemRequestDto> getItemRequestsFromOtherUsers(Long userId) {
		return itemRequestRepository.findAllByRequestorIdNotInOrderByCreatedDesc(userId)
				.stream()
				.map(ItemRequestMapper::toItemRequestDto)
				.toList();
	}

	@Override
	public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long ownerId) {
		Optional<User> optionalUser = userRepository.findById(ownerId);
		User owner = optionalUser.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, ownerId));

		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, owner);
		return ItemRequestMapper.toItemRequestDto(itemRequestRepository.saveAndFlush(itemRequest));
	}
}
