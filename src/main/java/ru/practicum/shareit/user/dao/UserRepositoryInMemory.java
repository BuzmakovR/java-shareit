package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserRepositoryInMemory implements UserRepository {

	private final Map<Long, User> users = new HashMap<>();

	@Override
	public User get(Long id) {
		if (!users.containsKey(id)) {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
		return users.get(id);
	}

	@Override
	public User add(User user) {
		user.setId(getNextId());
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(User updateUser) {
		if (!users.containsKey(updateUser.getId())) {
			throw new NotFoundException("Пользователь с id = " + updateUser.getId() + " не найден");
		}
		users.put(updateUser.getId(), updateUser);
		return updateUser;
	}

	@Override
	public void delete(Long id) {
		Optional<User> optionalUser = Optional.ofNullable(users.remove(id));
		if (optionalUser.isEmpty()) {
			throw new NotFoundException("Пользователь с id = " + id + " не найден");
		}
	}

	@Override
	public boolean checkExistsOtherUserByParams(Long userId, String email) {
		if (email == null || email.isBlank()) {
			return false;
		}
		return users.values().stream()
				.anyMatch(user -> email.equals(user.getEmail()) &&
						(userId == null || !Objects.equals(userId, user.getId())));
	}

	private long getNextId() {
		long currentMaxId = users.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
