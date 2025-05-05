package ru.practicum.shareit.booing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

	private static final String BASE_URL = "/bookings";

	@Mock
	private BookingClient bookingClient;

	@InjectMocks
	private BookingController bookingController;

	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	void getBookings() throws Exception {
		mockMvc.perform(get(BASE_URL)
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void getBooking() throws Exception {
		mockMvc.perform(get(BASE_URL + "/1")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void createBooking() throws Exception {
		BookItemRequestDto bookItemRequestDto = BookItemRequestDto.builder()
				.itemId(1L)
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.build();

		when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class)))
				.thenReturn(null);

		mockMvc.perform(post(BASE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(bookItemRequestDto))
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isCreated());
	}

	@Test
	void getBookingsByItemOwner() throws Exception {
		mockMvc.perform(get(BASE_URL + "/owner")
						.header("X-Sharer-User-Id", 1L))
				.andExpect(status().isOk());
	}

	@Test
	void approveBooking() throws Exception {
		mockMvc.perform(patch(BASE_URL + "/1")
						.header("X-Sharer-User-Id", 1L)
						.param("approved", "true"))
				.andExpect(status().isOk());
	}

}
