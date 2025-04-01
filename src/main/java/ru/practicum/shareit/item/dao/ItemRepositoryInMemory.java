package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class ItemRepositoryInMemory implements ItemRepository {

	private final Map<Long, Item> items = new HashMap<>();

	@Override
	public Item get(Long id) {
		if (!items.containsKey(id)) {
			throw new NotFoundException("Элемент с id = " + id + " не найден");
		}
		return items.get(id);
	}

	@Override
	public Item add(Item item) {
		item.setId(getNextId());
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Item update(Item updateItem) {
		if (!items.containsKey(updateItem.getId())) {
			throw new NotFoundException("Элемент с id = " + updateItem.getId() + " не найден");
		}
		items.put(updateItem.getId(), updateItem);
		return updateItem;
	}

	@Override
	public void delete(Long id) {
		Optional<Item> optionalItem = Optional.ofNullable(items.remove(id));
		if (optionalItem.isEmpty()) {
			throw new NotFoundException("Элемент с id = " + id + " не найден");
		}
	}

	@Override
	public Collection<Item> getByUserId(Long userId) {
		return items.values().stream()
				.filter(item -> Objects.equals(userId, item.getOwnerId()))
				.toList();
	}

	@Override
	public Collection<Item> search(String name) {
		name = name.toUpperCase();
		String finalName = name;
		return items.values().stream()
				.filter(item -> item.getIsAvailable() && item.getName().toUpperCase().contains(finalName))
				.toList();
	}

	private long getNextId() {
		long currentMaxId = items.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
