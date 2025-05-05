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
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	private static final String BASE_URL = "/users";

	@Mock
	private UserClient userClient;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private UserDto userDto;

	private UpdateUserRequest userRequest;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
		objectMapper = new ObjectMapper();
		userDto = UserDto.builder()
				.id(1L)
				.name("user")
				.email("email@mail.ru")
				.build();
	}

	@Test
	void createUser() throws Exception {
		when(userClient.addUser(any(UserDto.class))).thenReturn(null);

		mockMvc.perform(post(BASE_URL)
				.content(objectMapper.writeValueAsString(userDto))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isCreated());

		verify(userClient, Mockito.times(1)).addUser(any(UserDto.class));
	}

	@Test
	void createUserWithWrongEmail() throws Exception {
		userDto.setEmail("");

		mockMvc.perform(post(BASE_URL)
				.content(objectMapper.writeValueAsString(userDto))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());

		verify(userClient, never()).addUser(any(UserDto.class));

		userDto.setEmail("email");

		mockMvc.perform(post(BASE_URL)
				.content(objectMapper.writeValueAsString(userDto))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());

		verify(userClient, never()).addUser(any(UserDto.class));
	}

	@Test
	void getUser() throws Exception {
		mockMvc.perform(get(BASE_URL + "/{userId}", anyLong())
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk());

		verify(userClient, Mockito.times(1)).getUser(anyLong());
	}

	@Test
	void updateUser() throws Exception {
		when(userClient.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(null);

		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_update")
				.email("update_" + userDto.getEmail())
				.build();

		mockMvc.perform(patch(BASE_URL + "/" + userDto.getId())
				.content(objectMapper.writeValueAsString(updateUserRequest))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk());

		verify(userClient, Mockito.times(1)).updateUser(anyLong(), any(UpdateUserRequest.class));
	}

	@Test
	void updateUserWrongEmail() throws Exception {
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_update")
				.email("update_email")
				.build();

		mockMvc.perform(patch(BASE_URL + "/" + userDto.getId())
				.content(objectMapper.writeValueAsString(updateUserRequest))
				.characterEncoding(StandardCharsets.UTF_8)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());
	}

	@Test
	void deleteUser() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/{userId}", userDto.getId()))
				.andExpect(status().isOk());

		verify(userClient, Mockito.times(1)).deleteUser(userDto.getId());
	}
}
