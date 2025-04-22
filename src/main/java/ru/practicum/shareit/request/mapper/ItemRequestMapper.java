package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return ItemRequestDto.builder()
				.description(itemRequest.getDescription())
				.requestor(itemRequest.getRequestor())
				.created(itemRequest.getCreated())
				.build();
	}

	public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto, User requestor) {
		return ItemRequest.builder()
				.id(itemRequestDto.getId())
				.description(itemRequestDto.getDescription())
				.requestor(requestor)
				.build();
	}
}
