package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class RequestMapper {

	public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
		return ItemRequestDto.builder()
				.description(itemRequest.getDescription())
				.requestor(itemRequest.getRequestor())
				.created(itemRequest.getCreated())
				.build();
	}
}
