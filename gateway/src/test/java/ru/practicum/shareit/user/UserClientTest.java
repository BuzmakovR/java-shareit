package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

	private UserClient userClient;

	private UserDto userDto;

	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
		when(builder.build()).thenReturn(restTemplate);
		when(builder.uriTemplateHandler(any())).thenReturn(builder);
		when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

		String serverUrl = "http://localhost:8080";
		userClient = new UserClient(serverUrl, builder);
		userDto = UserDto.builder()
				.id(1L)
				.name("user")
				.email("email@mail.ru")
				.build();
	}

	@Test
	void getUser() {
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		long userId = 1L;
		String expectedUrl = "/" + userId;

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.GET),
				argThat(entity ->
						Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
								entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
				),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = userClient.getUser(userId);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void createUser() {
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userDto);

		String expectedUrl = "";
		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.POST),
				argThat(entity ->
						Objects.equals(entity.getBody(), userDto) &&
								Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
								entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
				),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = userClient.addUser(userDto);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void updateUser() {
		UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
				.name(userDto.getName() + "_updated")
				.email("updated_" + userDto.getEmail())
				.build();
		UserDto userDtoUpdated = UserDto.builder()
				.id(userDto.getId())
				.name(updateUserRequest.getName())
				.email(updateUserRequest.getEmail())
				.build();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(userDtoUpdated);
		long userId = 1L;
		String expectedUrl = "/" + userId;

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.PATCH),
				argThat(entity ->
						Objects.equals(entity.getBody(), updateUserRequest) &&
								Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
								entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
				),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = userClient.updateUser(userId, updateUserRequest);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void deleteUser() {
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		long userId = 1L;
		String expectedUrl = "/" + userId;

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.DELETE),
				argThat(entity ->
						Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
								entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON)
				),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = userClient.deleteUser(userId);

		assertEquals(expectedResponse, actualResponse);
	}
}
