package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

	UserDto getUser(Long id);

	UserDto addUser(UserDto userDto);

	UserDto updateUser(Long userId, UpdateUserRequest user);

	void deleteUser(Long id);
}
