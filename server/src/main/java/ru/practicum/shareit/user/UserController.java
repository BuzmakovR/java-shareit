package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/{id}")
	public UserDto get(@PathVariable("id") long id) {
		return userService.getUser(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto create(@RequestBody UserDto userDto) {
		return userService.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public UserDto update(@PathVariable long userId, @RequestBody UpdateUserRequest userRequest) {
		return userService.updateUser(userId, userRequest);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") long userId) {
		userService.deleteUser(userId);
	}
}
