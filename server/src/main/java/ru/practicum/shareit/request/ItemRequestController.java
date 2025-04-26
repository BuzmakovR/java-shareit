package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

	private final ItemRequestService itemRequestService;

	@GetMapping
	public Collection<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
		return itemRequestService.getItemRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ItemRequestDto get(@PathVariable long requestId,
			@RequestHeader("X-Sharer-User-Id") long userId) {
		return itemRequestService.getItemRequest(requestId);
	}

	@GetMapping("/all")
	public Collection<ItemRequestDto> getRequestsFromOtherUsers(@RequestHeader("X-Sharer-User-Id") long userId) {
		return itemRequestService.getItemRequestsFromOtherUsers(userId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
								 @RequestHeader("X-Sharer-User-Id") long userId) {

		return itemRequestService.addItemRequest(itemRequestDto, userId);
	}
}
