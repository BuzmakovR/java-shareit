package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public class ItemRequestMapper {

	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return ItemRequestDto.builder()
				.id(itemRequest.getId())
				.description(itemRequest.getDescription())
				.requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
				.created(itemRequest.getCreated())
				.build();
	}

	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, Collection<Item> items) {
		ItemRequestDto itemRequestDto = toItemRequestDto(itemRequest);
		itemRequestDto.setItems(
				items.stream()
						.map(ItemMapper::toItemForRequestDto)
						.toList());
		return itemRequestDto;
	}

	public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto, User requestor) {
		return ItemRequest.builder()
				.id(itemRequestDto.getId())
				.description(itemRequestDto.getDescription())
				.requestor(requestor)
				.build();
	}

}
