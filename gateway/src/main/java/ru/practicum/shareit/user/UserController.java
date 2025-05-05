package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

	private final UserClient userClient;

	@GetMapping("/{userId}")
	public ResponseEntity<Object> get(@PathVariable long userId) {
		log.info("Get user: userId={}", userId);
		return userClient.getUser(userId);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
		log.info("Create user: user={}", userDto);
		return userClient.addUser(userDto);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> update(@PathVariable long userId, @Valid @RequestBody UpdateUserRequest userRequest) {
		log.info("Update user: userId={}, userData={}", userId, userRequest);
		return userClient.updateUser(userId, userRequest);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") long userId) {
		log.info("Delete user: userId={}", userId);
		return userClient.deleteUser(userId);
	}
}
