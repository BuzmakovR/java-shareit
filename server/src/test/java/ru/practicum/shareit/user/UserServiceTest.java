package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	private Long userCount = 1L;

	@Test
	void getExistsUserById() {
		User user = getNewUser();

		when(userRepository.findById(user.getId()))
				.thenReturn(Optional.of(user));

		UserDto userDto = userService.getUser(user.getId());

		assertNotNull(userDto);
		assertEquals(user.getId(), userDto.getId());
		assertEquals(user.getName(), userDto.getName());
		assertEquals(user.getEmail(), userDto.getEmail());
		verify(userRepository, times(1)).findById(user.getId());
	}

	@Test
	void getNotExistsUserById() {
		Long notExistsUserId = -1L;

		when(userRepository.findById(notExistsUserId))
				.thenThrow(new NotFoundException("", notExistsUserId));

		assertThrows(NotFoundException.class, () -> userService.getUser(notExistsUserId));
		verify(userRepository, times(1)).findById(notExistsUserId);
	}

	@Test
	void addUser() {
		User user = getNewUser();
		UserDto userDto = UserMapper.toUserDto(user);

		when(userRepository.saveAndFlush(any()))
				.thenReturn(user);

		UserDto userDtoCreated = userService.addUser(userDto);
		assertNotNull(userDtoCreated);
		assertEquals(userDto.getId(), userDtoCreated.getId());
		assertEquals(userDto.getName(), userDtoCreated.getName());
		assertEquals(userDto.getEmail(), userDtoCreated.getEmail());
		verify(userRepository, times(1)).saveAndFlush(any());
	}

	@Test
	void updateExistsUser() {
		UserDto userDto = getNewUserDto();
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_updated")
				.email("updated_" + userDto.getEmail())
				.build();
		User userUpdated = User.builder()
				.id(userDto.getId())
				.name(updateUserRequest.getName())
				.email(updateUserRequest.getEmail())
				.build();
		UserDto userDtoUpdated = UserMapper.toUserDto(userUpdated);
		when(userRepository.findById(userDto.getId()))
				.thenReturn(Optional.of(UserMapper.fromUserDto(userDto)));
		when(userRepository.saveAndFlush(any()))
				.thenReturn(userUpdated);

		UserDto userDtoUpdatedResult = userService.updateUser(userDto.getId(), updateUserRequest);
		assertNotNull(userDtoUpdatedResult);
		assertEquals(userDtoUpdated.getId(), userDtoUpdatedResult.getId());
		assertEquals(userDtoUpdated.getName(), userDtoUpdatedResult.getName());
		assertEquals(userDtoUpdated.getEmail(), userDtoUpdatedResult.getEmail());
		verify(userRepository, times(1)).saveAndFlush(any());
	}

	@Test
	void updateNotExistsUser() {
		UserDto userDto = getNewUserDto();
		userDto.setId(-1L);

		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_updated")
				.email("updated_" + userDto.getEmail())
				.build();
		UserDto userDtoUpdated = UserDto.builder()
				.id(userDto.getId())
				.name(updateUserRequest.getName())
				.email(updateUserRequest.getEmail())
				.build();
		when(userRepository.findById(userDto.getId()))
				.thenThrow(new NotFoundException(""));

		assertThrows(NotFoundException.class, () -> userService.updateUser(userDto.getId(), updateUserRequest));
	}

	@Test
	void deleteExistsUser() {
		User user = getNewUser();

		assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
		verify(userRepository, times(1)).deleteById(user.getId());
	}

	private User getNewUser() {
		userCount++;
		return User.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
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
