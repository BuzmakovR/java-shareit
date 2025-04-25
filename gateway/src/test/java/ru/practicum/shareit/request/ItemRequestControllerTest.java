package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

	private static final String BASE_URL = "/requests";

	@Mock
	private ItemRequestClient itemRequestClient;

	@InjectMocks
	private ItemRequestController itemRequestController;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
		objectMapper = new ObjectMapper();
	}

	@Test
	void createItemRequest() throws Exception {
		ItemRequestDto requestDto = ItemRequestDto.builder()
				.description("desc")
				.build();

		when(itemRequestClient.addItemRequest(any(ItemRequestDto.class), anyLong()))
				.thenReturn(null);

		mockMvc.perform(post(BASE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isCreated());
	}

	@Test
	void getItemRequests() throws Exception {
		mockMvc.perform(get(BASE_URL)
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getItemRequest() throws Exception {
		mockMvc.perform(get(BASE_URL + "/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getRequestsFromOtherUsers() throws Exception {
		mockMvc.perform(get(BASE_URL + "/all")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

}
