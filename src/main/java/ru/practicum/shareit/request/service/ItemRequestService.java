package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

	ItemRequestDto getItemRequest(Long id);

	Collection<ItemRequestDto> getItemRequests(Long userId);

	Collection<ItemRequestDto> getItemRequestsFromOtherUsers(Long userId);

	ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long ownerId);
}
