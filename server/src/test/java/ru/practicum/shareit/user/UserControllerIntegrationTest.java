package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserControllerIntegrationTest {

	private Long userCount = 1L;

	@Autowired
	private UserController userController;

	@Test
	void createUser() {
		UserDto userDto = getNewUserDto();
		try {
			userDto = userController.create(userDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(userDto.getId());
	}

	@Test
	void createUserConflictEmail() {
		UserDto userDto = getNewUserDto();
		try {
			userDto = userController.create(userDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(userDto.getId());

		UserDto userDto2 = getNewUserDto();
		userDto2.setEmail(userDto.getEmail());

		assertThrows(ConflictException.class, () -> {
			userController.create(userDto2);
		}, "Не получено исключение ConflictException");
	}

	@Test
	void getUserByExistsId() {
		UserDto userDto = getNewUserDto();
		try {
			userDto = userController.create(userDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

		UserDto userDtoFromController = null;
		try {
			userDtoFromController = userController.get(userDto.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(userDtoFromController);
		assertEquals(userDto, userDtoFromController);
	}

	@Test
	void getUserByNotExistsId() {
		assertThrows(NotFoundException.class, () -> userController.get(-1L), "Не получено исключение NotFoundException при попытке получение несуществующего пользователя");
	}

	@Test
	void updateUser() {
		UserDto userDto = getNewUserDto();
		try {
			userDto = userController.create(userDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_updated")
				.email(userDto.getEmail())
				.build();
		UserDto updatedUserDto = UserDto.builder()
				.id(userDto.getId())
				.name(updateUserRequest.getName())
				.email(updateUserRequest.getEmail())
				.build();
		try {
			userDto = userController.update(userDto.getId(), updateUserRequest);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertEquals(userDto, updatedUserDto);
	}

	@Test
	void deleteUser() {
		UserDto userDto = getNewUserDto();
		try {
			userDto = userController.create(userDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		try {
			userController.delete(userDto.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
	}
}
