package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserService userService;

	private final ObjectMapper mapper = new ObjectMapper();

	private MockMvc mockMvc;

	private Long userCount = 1L;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(userController)
				.setControllerAdvice(new ErrorHandler())
				.build();
	}

	@Test
	void createUser() throws Exception {
		UserDto userDto = getNewUserDto();
		when(userService.addUser(any()))
				.thenReturn(userDto);

		mockMvc.perform(post("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(userDto))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name").value(userDto.getName()))
				.andExpect(jsonPath("$.email").value(userDto.getEmail()));

		Mockito.verify(userService, Mockito.times(1))
				.addUser(any());
	}

	@Test
	void createUserConflictEmail() throws Exception {
		UserDto userDto = getNewUserDto();
		when(userService.addUser(any()))
				.thenThrow(new ConflictException("Нарушена уникальность пользователей по email"));

		mockMvc.perform(post("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(userDto))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Нарушена уникальность пользователей по email"));

		Mockito.verify(userService, Mockito.times(1))
				.addUser(any());
	}

	@Test
	void getUserByExistsId() throws Exception {
		UserDto userDto = getNewUserDto();
		when(userService.getUser(anyLong()))
				.thenReturn(userDto);

		mockMvc.perform(get("/users/{userId}", userDto.getId())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userDto.getId()))
				.andExpect(jsonPath("$.name").value(userDto.getName()))
				.andExpect(jsonPath("$.email").value(userDto.getEmail()));

		Mockito.verify(userService, Mockito.times(1))
				.getUser(anyLong());
	}

	@Test
	void getUserByNotExistsId() throws Exception {
		when(userService.getUser(anyLong()))
				.thenThrow(new NotFoundException(""));

		mockMvc.perform(get("/users/{userId}", -1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		Mockito.verify(userService, Mockito.times(1))
				.getUser(anyLong());
	}

	@Test
	void updateUser() throws Exception {
		UserDto userDto = getNewUserDto();
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_updated")
				.email(userDto.getEmail())
				.build();
		UserDto userDtoUpdated = UserDto.builder()
				.id(userDto.getId())
				.name(updateUserRequest.getName())
				.email(updateUserRequest.getEmail())
				.build();

		when(userService.updateUser(anyLong(), any()))
				.thenReturn(userDtoUpdated);

		mockMvc.perform(patch("/users/{userId}", userDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(updateUserRequest))
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(userDtoUpdated.getId()))
				.andExpect(jsonPath("$.name").value(userDtoUpdated.getName()))
				.andExpect(jsonPath("$.email").value(userDtoUpdated.getEmail()));

		Mockito.verify(userService, Mockito.times(1))
				.updateUser(anyLong(), any());
	}

	@Test
	void updateUserWithConflictEmail() throws Exception {
		UserDto userDto = getNewUserDto();
		UserDto userDtoForUpdate = getNewUserDto();
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDtoForUpdate.getName() + "_updated")
				.email(userDto.getEmail())
				.build();

		when(userService.updateUser(anyLong(), any()))
				.thenThrow(new ConflictException("Нарушена уникальность пользователей по email"));

		mockMvc.perform(patch("/users/{userId}", userDtoForUpdate.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(updateUserRequest))
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Нарушена уникальность пользователей по email"));

		Mockito.verify(userService, Mockito.times(1))
				.updateUser(anyLong(), any());
	}

	@Test
	void deleteUser() throws Exception {
		Long userId = 1L;

		mockMvc.perform(delete("/users/{id}", userId))
				.andExpect(status().isOk());

		Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
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
