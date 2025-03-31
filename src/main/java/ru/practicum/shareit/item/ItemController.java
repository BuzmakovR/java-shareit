package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

	private final ItemService itemService;

	@GetMapping()
	public Collection<ItemDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
		return itemService.getItemsByUser(userId);
	}

	@GetMapping("/{itemId}")
	public ItemDto get(@RequestHeader("X-Sharer-User-Id") long userId,
					   @PathVariable long itemId) {
		return itemService.getItem(itemId);
	}

	@GetMapping("/search")
	public Collection<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId,
									  @RequestParam("text") String text) {
		return itemService.search(text);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
						  @Valid @RequestBody Item item) {
		return itemService.addItem(item, userId);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
						  @PathVariable long itemId,
						  @Valid @RequestBody UpdateItemRequest updateItem) {
		return itemService.updateItem(itemId, updateItem, userId);
	}

	@DeleteMapping("/{itemId}")
	public ItemDto delete(@RequestHeader("X-Sharer-User-Id") long userId,
						  @PathVariable long itemId) {
		return itemService.deleteItem(itemId, userId);
	}

}
