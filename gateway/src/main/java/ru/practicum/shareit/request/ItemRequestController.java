package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

	private final ItemRequestClient itemRequestClient;

	@GetMapping
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Get requests: userId={}", userId);
		return itemRequestClient.getItemRequests(userId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
							  @PathVariable long requestId) {
		log.info("Get request: requestId={}", requestId);
		return itemRequestClient.getItemRequest(requestId, userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getRequestsFromOtherUsers(@RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Get request from other users: userId={}", userId);
		return itemRequestClient.getItemRequestsFromOtherUsers(userId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
								 @Valid @RequestBody ItemRequestDto itemRequestDto) {
		log.info("Create request: userId={}, item={}", userId, itemRequestDto);
		return itemRequestClient.addItemRequest(itemRequestDto, userId);
	}
}
