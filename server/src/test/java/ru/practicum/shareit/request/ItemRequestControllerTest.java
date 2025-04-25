package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

	@InjectMocks
	private ItemRequestController itemRequestController;

	@Mock
	private ItemRequestService itemRequestService;

	@Mock
	private UserService userService;

	private final ObjectMapper mapper = new ObjectMapper();

	private MockMvc mockMvc;

	private Long itemRequestCount = 1L;

	private Long userCount = 1L;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(itemRequestController)
				.setControllerAdvice(new ErrorHandler())
				.build();
	}

	@Test
	void createItemRequest() throws Exception {
		UserDto userDto = getNewUserDto();
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);

		when(itemRequestService.addItemRequest(itemRequestDto, userDto.getId()))
				.thenReturn(itemRequestDto);

		mockMvc.perform(post("/requests")
						.header("X-Sharer-User-Id", userDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(itemRequestDto))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
				.andExpect(jsonPath("$.requestor.id").value(itemRequestDto.getRequestor().getId()));

		Mockito.verify(itemRequestService, Mockito.times(1))
				.addItemRequest(itemRequestDto, userDto.getId());
	}

	@Test
	void getItemRequest() throws Exception {
		UserDto userDto = getNewUserDto();
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);

		when(itemRequestService.getItemRequest(itemRequestDto.getId()))
				.thenReturn(itemRequestDto);

		mockMvc.perform(get("/requests/" + itemRequestDto.getId())
						.header("X-Sharer-User-Id", userDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
				.andExpect(jsonPath("$.requestor.id").value(itemRequestDto.getRequestor().getId()));

		Mockito.verify(itemRequestService, Mockito.times(1))
				.getItemRequest(itemRequestDto.getId());
	}

	@Test
	void getUserItemRequests() throws Exception {
		UserDto userDto = getNewUserDto();
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);

		when(itemRequestService.getItemRequests(userDto.getId()))
				.thenReturn(List.of(itemRequestDto));

		mockMvc.perform(get("/requests")
						.header("X-Sharer-User-Id", userDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").isNotEmpty())
				.andExpect(jsonPath("$[*].description").value(itemRequestDto.getDescription()))
				.andExpect(jsonPath("$[*].requestor.id").value(userDto.getId().intValue()));

		Mockito.verify(itemRequestService, Mockito.times(1))
				.getItemRequests(userDto.getId());
	}

	@Test
	void getOtherUserItemRequests() throws Exception {
		UserDto userDto = getNewUserDto();
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);

		UserDto otherUserDto = getNewUserDto();
		ItemRequestDto itemRequestDtoOtherUser = getNewItemRequestDto(otherUserDto);

		when(itemRequestService.getItemRequests(userDto.getId()))
				.thenReturn(List.of(itemRequestDtoOtherUser));

		mockMvc.perform(get("/requests")
						.header("X-Sharer-User-Id", userDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").isNotEmpty())
				.andExpect(jsonPath("$[*].description").value(itemRequestDtoOtherUser.getDescription()))
				.andExpect(jsonPath("$[*].requestor.id").value(otherUserDto.getId().intValue()));

		Mockito.verify(itemRequestService, Mockito.times(1))
				.getItemRequests(userDto.getId());

		when(itemRequestService.getItemRequests(otherUserDto.getId()))
				.thenReturn(List.of(itemRequestDto));

		mockMvc.perform(get("/requests")
						.header("X-Sharer-User-Id", otherUserDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").isNotEmpty())
				.andExpect(jsonPath("$[*].description").value(itemRequestDto.getDescription()))
				.andExpect(jsonPath("$[*].requestor.id").value(userDto.getId().intValue()));

		Mockito.verify(itemRequestService, Mockito.times(1))
				.getItemRequests(otherUserDto.getId());
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
	}

	private ItemRequestDto getNewItemRequestDto(UserDto userDto) {
		itemRequestCount++;
		return ItemRequestDto.builder()
				.id(itemRequestCount)
				.description("text_" + itemRequestCount)
				.requestor(userDto)
				.build();
	}
}
