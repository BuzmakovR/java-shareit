package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

	UserDto getUser(Long id);

	UserDto addUser(User user);

	UserDto updateUser(Long userId, UpdateUserRequest user);

	UserDto deleteUser(Long id);
}
