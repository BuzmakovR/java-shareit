package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemClientTest {

	private ItemClient itemClient;

	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	void setUp() {
		RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);
		when(builder.build()).thenReturn(restTemplate);
		when(builder.uriTemplateHandler(any())).thenReturn(builder);
		when(builder.requestFactory(any(Supplier.class))).thenReturn(builder);

		String serverUrl = "http://localhost:8080";
		itemClient = new ItemClient(serverUrl, builder);
	}

	@Test
	void getItem() {
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
		long itemId = 1L;
		long userId = 1L;
		String expectedUrl = "/" + itemId;

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.GET),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId))),
				eq(Object.class)
		)).thenReturn(expectedResponse);

		ResponseEntity<Object> actualResponse = itemClient.getItem(itemId, userId);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void createItem() {
		long userId = 1L;
		String expectedUrl = "";

		CreateItemRequest createItemRequest = CreateItemRequest.builder()
				.name("name")
				.description("desc")
				.isAvailable(true)
				.build();
		ItemDto itemDto = ItemDto.builder()
				.name(createItemRequest.getName())
				.description(createItemRequest.getDescription())
				.isAvailable(createItemRequest.getIsAvailable())
				.build();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(itemDto);

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.POST),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId)) &&
						Objects.equals(entity.getBody(), createItemRequest)),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = itemClient.addItem(createItemRequest, userId);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void updateItem() {
		long itemId = 1L;
		long userId = 1L;
		String expectedUrl = "/" + itemId;

		UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
				.name("updated")
				.description("updated")
				.isAvailable(true)
				.build();

		ItemDto itemDto = ItemDto.builder()
				.id(itemId)
				.name(updateItemRequest.getName())
				.description(updateItemRequest.getDescription())
				.isAvailable(updateItemRequest.getIsAvailable())
				.build();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(itemDto);

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.PATCH),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId)) &&
						Objects.equals(entity.getBody(), updateItemRequest)),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = itemClient.updateItem(itemId, updateItemRequest, userId);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void createComment() {
		long itemId = 1L;
		long userId = 1L;
		String expectedUrl = "/" + itemId + "/comment";

		CommentDto commentDto = CommentDto.builder()
				.text("comment")
				.build();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(commentDto);

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.POST),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId)) &&
						Objects.equals(entity.getBody(), commentDto)),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = itemClient.addComment(commentDto, itemId, userId);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void searchItem() {
		long userId = 1L;
		String searchText = "text";
		String expectedUrl = "/search?text={text}";
		ItemDto itemDto = ItemDto.builder()
				.name("text")
				.description("text")
				.isAvailable(true)
				.build();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok(List.of(itemDto));

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.GET),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId))),
				eq(Object.class),
				eq(Map.of("text", searchText))
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = itemClient.search(userId, searchText);

		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void getItemsByUser() {
		long userId = 1L;
		String expectedUrl = "";
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(
				eq(expectedUrl),
				eq(HttpMethod.GET),
				argThat(entity -> Objects.equals(entity.getHeaders().getContentType(), MediaType.APPLICATION_JSON) &&
						entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON) &&
						Objects.equals(entity.getHeaders().getFirst("X-Sharer-User-Id"), String.valueOf(userId))),
				eq(Object.class)
		)).thenReturn(expectedResponse);
		ResponseEntity<Object> actualResponse = itemClient.getItemsByUser(userId);

		assertEquals(expectedResponse, actualResponse);
	}

}
