package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

	private static final String BASE_URL = "/items";

	@Mock
	private ItemClient itemClient;

	@InjectMocks
	private ItemController itemController;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void getItemsByUser() throws Exception {
		mockMvc.perform(get(BASE_URL)
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getItem() throws Exception {
		mockMvc.perform(get(BASE_URL + "/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void searchItem() throws Exception {
		mockMvc.perform(get(BASE_URL + "/search")
						.param("text", "searchText")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void createItem() throws Exception {
		CreateItemRequest createItemRequest = CreateItemRequest.builder()
				.name("name")
				.description("desc")
				.isAvailable(true)
				.build();

		when(itemClient.addItem(any(CreateItemRequest.class), anyLong())).thenReturn(null);

		mockMvc.perform(post(BASE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createItemRequest))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isCreated());
	}

	@Test
	void createItemInvalid() throws Exception {
		CreateItemRequest createItemRequest = CreateItemRequest.builder().build();

		mockMvc.perform(post(BASE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(createItemRequest))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateItem() throws Exception {
		UpdateItemRequest updateUserRequest = UpdateItemRequest.builder()
				.name("updated")
				.description("updated")
				.build();

		mockMvc.perform(patch(BASE_URL + "/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateUserRequest))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void updateItemWithoutUser() throws Exception {
		UpdateItemRequest updateUserRequest = UpdateItemRequest.builder()
				.name("updated")
				.description("updated")
				.build();

		mockMvc.perform(patch(BASE_URL + "/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updateUserRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createComment() throws Exception {
		CommentDto commentDto = CommentDto.builder()
				.text("comment")
				.build();

		when(itemClient.addComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(null);

		mockMvc.perform(post(BASE_URL + "/1/comment")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentDto))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isCreated());
	}
}
