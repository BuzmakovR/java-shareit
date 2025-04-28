package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

	@InjectMocks
	private ItemController itemController;

	@Mock
	private UserService userService;

	@Mock
	private ItemService itemService;

	private final ObjectMapper mapper = new ObjectMapper();

	private MockMvc mockMvc;

	private Long userCount = 1L;

	private Long itemCount = 1L;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(itemController)
				.setControllerAdvice(new ErrorHandler())
				.build();
	}

	@Test
	void createItem() throws Exception {
		UserDto userDto = getNewUserDto();
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto itemDto = ItemDto.builder()
				.id(itemCount++)
				.name(createItemRequest.getName())
				.description(createItemRequest.getDescription())
				.isAvailable(createItemRequest.getIsAvailable())
				.build();

		when(itemService.addItem(createItemRequest, userDto.getId()))
				.thenReturn(itemDto);

		mockMvc.perform(post("/items")
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(createItemRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", userDto.getId())
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name").value(createItemRequest.getName()))
				.andExpect(jsonPath("$.description").value(createItemRequest.getDescription()));

		Mockito.verify(itemService, Mockito.times(1))
				.addItem(createItemRequest, userDto.getId());
	}

	@Test
	void updateItem() throws Exception {
		UserDto userDto = getNewUserDto();
		ItemDto itemDto = ItemDto.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();
		UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
				.name(itemDto.getName() + "_updated")
				.description(itemDto.getDescription() + "_updated")
				.build();
		ItemDto updatedItemDto = ItemDto.builder()
				.id(itemDto.getId())
				.name(updateItemRequest.getName())
				.description(updateItemRequest.getDescription())
				.isAvailable(updateItemRequest.getIsAvailable())
				.build();

		when(itemService.updateItem(itemDto.getId(), updateItemRequest, userDto.getId()))
				.thenReturn(updatedItemDto);

		mockMvc.perform(patch("/items/" + itemDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(updateItemRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", userDto.getId())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name").value(updatedItemDto.getName()))
				.andExpect(jsonPath("$.description").value(updatedItemDto.getDescription()));

		Mockito.verify(itemService, Mockito.times(1))
				.updateItem(itemDto.getId(), updateItemRequest, userDto.getId());
	}

	@Test
	void deleteItem() throws Exception {
		Long itemId = 1L;
		Long userId = 1L;

		mockMvc.perform(delete("/items/{id}", itemId)
						.header("X-Sharer-User-Id", userId))
				.andExpect(status().isOk());

		Mockito.verify(itemService, Mockito.times(1)).deleteItem(itemId, userId);
	}

	@Test
	void getItem() throws Exception {
		UserDto userDto = getNewUserDto();
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto itemDto = ItemDto.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();

		when(itemService.getItem(itemDto.getId(), userDto.getId()))
				.thenReturn(itemDto);

		mockMvc.perform(get("/items/" + itemDto.getId())
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(createItemRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", userDto.getId())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(itemDto.getId()))
				.andExpect(jsonPath("$.name").value(itemDto.getName()))
				.andExpect(jsonPath("$.description").value(itemDto.getDescription()));

		Mockito.verify(itemService, Mockito.times(1))
				.getItem(itemDto.getId(), userDto.getId());
	}

	@Test
	void getItems() throws Exception {
		UserDto userDto = getNewUserDto();
		CreateItemRequest createItemRequest = createItemRequest();
		ItemOwnerDto itemDto = ItemOwnerDto.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();

		when(itemService.getItemsByUser(userDto.getId()))
				.thenReturn(List.of(itemDto));

		mockMvc.perform(get("/items")
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(createItemRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", userDto.getId())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").value(itemDto.getId().intValue()))
				.andExpect(jsonPath("$[*].name").value(itemDto.getName()))
				.andExpect(jsonPath("$[*].description").value(itemDto.getDescription()));

		Mockito.verify(itemService, Mockito.times(1))
				.getItemsByUser(userDto.getId());
	}

	@Test
	void searchItems() throws Exception {
		UserDto userDto = getNewUserDto();
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto itemDto = ItemDto.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();
		String searchText = "name";

		when(itemService.search(searchText, userDto.getId()))
				.thenReturn(List.of(itemDto));

		mockMvc.perform(get("/items/search?text=" + searchText)
						.characterEncoding(StandardCharsets.UTF_8)
						.content(mapper.writeValueAsString(createItemRequest))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.header("X-Sharer-User-Id", userDto.getId())
				).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id").value(itemDto.getId().intValue()))
				.andExpect(jsonPath("$[*].name").value(itemDto.getName()))
				.andExpect(jsonPath("$[*].description").value(itemDto.getDescription()));

		Mockito.verify(itemService, Mockito.times(1))
				.search(searchText, userDto.getId());
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
	}

	private CreateItemRequest createItemRequest() {
		return CreateItemRequest.builder()
				.name("name")
				.description("desc")
				.isAvailable(true)
				.build();
	}
}
