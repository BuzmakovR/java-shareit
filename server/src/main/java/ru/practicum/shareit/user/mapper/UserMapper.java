package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

	public static UserDto toUserDto(User user) {
		return UserDto.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.build();
	}

	public static User fromUserDto(UserDto userDto) {
		return User.builder()
				.id(userDto.getId())
				.email(userDto.getEmail())
				.name(userDto.getName())
				.build();
	}
}
