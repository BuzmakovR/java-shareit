package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private static final String NOT_FOUND_USER_BY_ID = "Пользователь не найден: ID = %d";

	@Override
	public UserDto getUser(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		return UserMapper.toUserDto(optionalUser.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, id)));
	}

	@Override
	public UserDto addUser(UserDto userDto) {
		User user = UserMapper.fromUserDto(userDto);
		validateUser(user);
		return UserMapper.toUserDto(userRepository.saveAndFlush(user));
	}

	@Override
	public UserDto updateUser(Long userId, UpdateUserRequest userRequest) {
		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId));

		if (userRequest.getName() != null && !userRequest.getName().isBlank()) {
			user.setName(userRequest.getName());
		}
		if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
			user.setEmail(userRequest.getEmail());
		}
		validateUser(user);
		return UserMapper.toUserDto(userRepository.saveAndFlush(user));
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public void validateUser(User user) {
		if (user.getEmail() == null || user.getEmail().isBlank()) {
			throw new ValidationException("Email пользователя должен быть заполнен");
		}
		Optional<User> userOptional = userRepository.findAllByEmail(user.getEmail());
		if (userOptional.isPresent() && !Objects.equals(user.getId(), userOptional.get().getId())) {
			throw new ConflictException("Нарушена уникальность пользователей по email");
		}
	}
}
