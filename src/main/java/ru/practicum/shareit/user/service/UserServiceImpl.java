package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public UserDto getUser(Long id) {
		return UserMapper.toUserDto(userRepository.get(id));
	}

	@Override
	public UserDto addUser(UserDto userDto) {
		if (userRepository.checkExistsOtherUserByParams(null, userDto.getEmail())) {
			throw new ConflictException("Нарушена уникальность пользователей");
		}
		User user = User.builder()
				.email(userDto.getEmail())
				.name(userDto.getName())
				.build();
		return UserMapper.toUserDto(userRepository.add(user));
	}

	@Override
	public UserDto updateUser(Long userId, UpdateUserRequest userRequest) {
		if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
			if (userRepository.checkExistsOtherUserByParams(userId, userRequest.getEmail())) {
				throw new ConflictException("Нарушена уникальность пользователей");
			}
		}
		User user = userRepository.get(userId);

		if (userRequest.getName() != null && !userRequest.getName().isBlank()) {
			user.setName(userRequest.getName());
		}
		if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
			user.setEmail(userRequest.getEmail());
		}
		return UserMapper.toUserDto(userRepository.update(user));
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.delete(id);
	}
}
