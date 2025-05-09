package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

	private static final String NOT_FOUND_ITEM_BY_ID = "Элемент не найден: ID = %d";
	private static final String NOT_FOUND_USER_BY_ID = "Пользователь не найден: ID = %d";
	private static final String NOT_FOUND_ITEM_REQUEST_BY_ID = "Запрос не найден: ID = %d";

	private final ItemRepository itemRepository;

	private final UserRepository userRepository;

	private final BookingRepository bookingRepository;

	private final CommentRepository commentRepository;

	private final ItemRequestRepository itemRequestRepository;

	@Override
	public ItemDto getItem(Long id, Long userId) {
		userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId));
		Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM_BY_ID, id));
		Collection<Comment> comments = commentRepository.findAllByItemIdIn(Set.of(id));
		return ItemMapper.toItemDto(item, comments);
	}

	@Override
	public ItemDto addItem(CreateItemRequest createItemRequest, Long ownerId) {
		User owner = userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, ownerId));
		ItemRequest itemRequest = null;
		if (createItemRequest.getRequestId() != null) {
			itemRequest = itemRequestRepository.findById(createItemRequest.getRequestId())
					.orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM_REQUEST_BY_ID, createItemRequest.getRequestId()));
		}
		return ItemMapper.toItemDto(
				itemRepository.saveAndFlush(
						ItemMapper.fromCreateItemRequest(createItemRequest, owner, itemRequest)));
	}

	@Override
	public ItemDto updateItem(Long itemId, UpdateItemRequest itemRequest, Long userId) {
		Item item = itemRepository.findByIdAndOwnerId(itemId, userId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM_BY_ID, itemId));
		if (itemRequest.getName() != null && !itemRequest.getName().isBlank()) {
			item.setName(itemRequest.getName());
		}
		if (itemRequest.getDescription() != null && !itemRequest.getDescription().isBlank()) {
			item.setDescription(itemRequest.getDescription());
		}
		if (itemRequest.getIsAvailable() != null) {
			item.setIsAvailable(itemRequest.getIsAvailable());
		}
		return ItemMapper.toItemDto(itemRepository.saveAndFlush(item));
	}

	@Override
	public void deleteItem(Long id, Long userId) {
		itemRepository.deleteById(id);
	}

	@Override
	public Collection<ItemOwnerDto> getItemsByUser(Long userId) {
		userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId));
		Collection<Item> items = itemRepository.findAllByOwnerId(userId);
		Collection<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
		Collection<Comment> comments = commentRepository.findAllByItemIdIn(
				items.stream()
						.map(Item::getId)
						.collect(Collectors.toSet()));

		return items.stream()
				.map(item -> ItemMapper.toItemOwnerDto(item, bookings, comments))
				.toList();
	}

	@Override
	public Collection<ItemDto> search(String name, Long userId) {
		userRepository.findById(userId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId));
		if (name == null || name.isBlank()) {
			return List.of();
		}
		return itemRepository.findAllUsingSearch(name).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
		Comment comment = CommentMapper.fromCommentDto(commentDto);
		comment.setAuthor(userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId)));
		comment.setItem(itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM_BY_ID, itemId)));
		comment.setCreated(LocalDateTime.now());

		if (bookingRepository
				.findAllByBookerIdAndItemIdAndEndBefore(userId, comment.getItem().getId(), LocalDateTime.now())
				.isEmpty()) {
			throw new ConditionsNotMetException("Не удалось найти доступных бронирований для добавления комментариев к нему");
		}
		return CommentMapper.toCommentDto(commentRepository.saveAndFlush(comment));
	}
}
