package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

	private static final String API_PREFIX = "/items";

	@Autowired
	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
						.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
						.build()
		);
	}

	public ResponseEntity<Object> getItem(long itemId, long userId) {
		return get("/" + itemId, userId);
	}

	public ResponseEntity<Object> getItemsByUser(long userId) {
		return get("", userId);
	}

	public ResponseEntity<Object> search(long userId, String text) {
		return get("/search?text={text}", userId, Map.of("text", text));
	}

	public ResponseEntity<Object> addItem(CreateItemRequest createItemRequest, long userId) {
		return post("", userId, createItemRequest);
	}

	public ResponseEntity<Object> addComment(CommentDto commentDto, long itemId, long userId) {
		return post("/" + itemId + "/comment", userId, commentDto);
	}

	public ResponseEntity<Object> updateItem(long itemId, UpdateItemRequest updateItem, long userId) {
		return patch("/" + itemId, userId, updateItem);
	}

	public ResponseEntity<Object> deleteItem(long itemId, long userId) {
		return delete("/" + itemId, userId);
	}
}
