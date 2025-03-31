package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserStorage userStorage;

	@Override
	public UserDto getUser(Long id) {
		return UserMapper.toUserDto(userStorage.get(id));
	}

	@Override
	public UserDto addUser(User user) {
		if (userStorage.checkExistsOtherUserByParams(null, user.getEmail())) {
			throw new ConflictException("Нарушена уникальность пользователей");
		}
		return UserMapper.toUserDto(userStorage.add(user));
	}

	@Override
	public UserDto updateUser(Long userId, UpdateUserRequest userRequest) {
		if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
			if (userStorage.checkExistsOtherUserByParams(userId, userRequest.getEmail())) {
				throw new ConflictException("Нарушена уникальность пользователей");
			}
		}
		User user = userStorage.get(userId);

		if (userRequest.getName() != null && !userRequest.getName().isBlank()) {
			user.setName(userRequest.getName());
		}
		if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
			user.setEmail(userRequest.getEmail());
		}
		return UserMapper.toUserDto(userStorage.update(user));
	}

	@Override
	public UserDto deleteUser(Long id) {
		return UserMapper.toUserDto(userStorage.delete(id));
	}
}
