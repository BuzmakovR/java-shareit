package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemRequestControllerIntegrationTest {

	private Long itemRequestCount = 1L;

	private Long userCount = 1L;

	@Autowired
	private final ItemRequestController itemRequestController;

	@Autowired
	private final UserService userService;

	@Test
	void createItemRequest() {
		UserDto userDto = userService.addUser(getNewUserDto());
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		try {
			itemRequestDto = itemRequestController.create(itemRequestDto.getRequestor().getId(), itemRequestDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemRequestDto.getId());
	}

	@Test
	void getItemRequest() {
		UserDto userDto = userService.addUser(getNewUserDto());
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		itemRequestDto = itemRequestController.create(itemRequestDto.getRequestor().getId(), itemRequestDto);
		ItemRequestDto itemRequestDtoResult = null;
		try {
			itemRequestDtoResult = itemRequestController.get(itemRequestDto.getRequestor().getId(), itemRequestDto.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemRequestDtoResult);
		assertEquals(itemRequestDto, itemRequestDtoResult);
	}

	@Test
	void getUserItemRequests() {
		UserDto userDto = userService.addUser(getNewUserDto());
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		itemRequestDto = itemRequestController.create(itemRequestDto.getRequestor().getId(), itemRequestDto);
		Collection<ItemRequestDto> itemRequests = null;
		try {
			itemRequests = itemRequestController.get(itemRequestDto.getRequestor().getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemRequests);
		assertEquals(1, itemRequests.size());
		assertEquals(itemRequestDto, itemRequests.stream().toList().getFirst());
	}

	@Test
	void getOtherUserItemRequests() {
		UserDto userDto = userService.addUser(getNewUserDto());
		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		itemRequestDto = itemRequestController.create(itemRequestDto.getRequestor().getId(), itemRequestDto);

		UserDto otherUserDto = userService.addUser(getNewUserDto());
		ItemRequestDto itemRequestDtoOtherUser = getNewItemRequestDto(otherUserDto);
		itemRequestDtoOtherUser = itemRequestController.create(itemRequestDtoOtherUser.getRequestor().getId(), itemRequestDtoOtherUser);

		Collection<ItemRequestDto> itemRequests = null;
		try {
			itemRequests = itemRequestController.getRequestsFromOtherUsers(itemRequestDto.getRequestor().getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemRequests);
		assertEquals(1, itemRequests.size());
		assertEquals(itemRequestDtoOtherUser, itemRequests.stream().toList().getFirst());
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
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
