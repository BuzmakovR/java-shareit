package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

	private final ItemClient itemClient;

	@GetMapping
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId) {
		log.info("Get items: userId={}", userId);
		return itemClient.getItemsByUser(userId);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
									  @PathVariable long itemId) {
		log.info("Get item: userId={}, itemId={}", userId, itemId);
		return itemClient.getItem(itemId, userId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
									  @RequestParam("text") String text) {
		log.info("Search item: userId={}, text={}", userId, text);
		return itemClient.search(userId, text);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
						  @Valid @RequestBody CreateItemRequest createItemRequest) {
		log.info("Add item: userId={}, itemData={}", userId, createItemRequest);
		return itemClient.addItem(createItemRequest, userId);
	}

	@PostMapping("/{itemId}/comment")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
									@PathVariable long itemId,
									@Valid @RequestBody CommentDto commentDto) {
		log.info("Add comment: userId={}, itemId={}, commentData={}", userId, itemId, commentDto);
		return itemClient.addComment(commentDto, itemId, userId);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
						  @PathVariable long itemId,
						  @Valid @RequestBody UpdateItemRequest updateItem) {
		log.info("Update item: userId={}, itemId={}, itemData={}", userId, itemId, updateItem);
		return itemClient.updateItem(itemId, updateItem, userId);
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity<Object> delete(@RequestHeader("X-Sharer-User-Id") long userId,
					   @PathVariable long itemId) {
		log.info("Delete item: userId={}, itemId={}", userId, itemId);
		return itemClient.deleteItem(itemId, userId);
	}
}
