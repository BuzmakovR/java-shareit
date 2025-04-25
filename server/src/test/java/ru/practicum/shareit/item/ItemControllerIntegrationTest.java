package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItServer.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class ItemControllerIntegrationTest {

	private Long userCount = 1L;

	@Autowired
	private final ItemController itemController;

	@Autowired
	private final UserService userService;

	@Autowired
	private final BookingService bookingService;

	@Test
	void createItem() {
		UserDto userDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();

		ItemDto item = null;
		try {
			item = itemController.create(userDto.getId(), createItemRequest);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(item.getId());
	}

	@Test
	void updateItem() {
		UserDto userDto = userService.addUser(getNewUserDto());

		CreateItemRequest createItemRequest = createItemRequest();

		UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
				.name(createItemRequest.getName() + "_updated")
				.description(createItemRequest.getDescription() + "_updated")
				.isAvailable(true)
				.build();

		ItemDto item = null;
		try {
			item = itemController.create(userDto.getId(), createItemRequest);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		ItemDto itemUpdated = null;
		try {
			itemUpdated = itemController.update(userDto.getId(), item.getId(), updateItemRequest);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemUpdated.getId());
		assertEquals(item.getId(), itemUpdated.getId());
		assertEquals(updateItemRequest.getName(), itemUpdated.getName());
		assertEquals(updateItemRequest.getDescription(), itemUpdated.getDescription());
	}

	@Test
	void deleteItem() {
		UserDto userDto = userService.addUser(getNewUserDto());
		ItemDto itemDto = itemController.create(userDto.getId(), createItemRequest());
		try {
			itemController.delete(userDto.getId(), itemDto.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void getItem() {
		UserDto userDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemController.create(userDto.getId(), createItemRequest);
		ItemDto itemDtoResult = null;
		try {
			itemDtoResult = itemController.get(userDto.getId(), item.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemDtoResult);
	}

	@Test
	void getNotExistsItem() {
		UserDto userDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemController.create(userDto.getId(), createItemRequest);
		Assertions.assertThrows(NotFoundException.class, () -> {
			itemController.get(userDto.getId(), item.getId() + 1);
		});
	}

	@Test
	void getItemsByUser() {
		UserDto userDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto itemDto = itemController.create(userDto.getId(), createItemRequest);
		Collection<ItemOwnerDto> itemDtoResult = null;
		try {
			itemDtoResult = itemController.get(userDto.getId());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemDtoResult);
		assertEquals(1, itemDtoResult.size());
		assertEquals(itemDto.getId(), itemDtoResult.stream().toList().getFirst().getId());
	}

	@Test
	void searchItems() {
		UserDto userDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto itemDto = itemController.create(userDto.getId(), createItemRequest);
		Collection<ItemDto> itemDtoResult = null;
		Collection<ItemDto> itemDtoEmptyResult = null;
		try {
			itemDtoResult = itemController.search(userDto.getId(), "name");
			itemDtoEmptyResult = itemController.search(userDto.getId(), "");
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(itemDtoResult);
		assertEquals(1, itemDtoResult.size());
		assertEquals(itemDto, itemDtoResult.stream().toList().getFirst());

		assertNotNull(itemDtoEmptyResult);
		assertEquals(0, itemDtoEmptyResult.size());
	}

	@Test
	void createComment() {
		UserDto userDto = userService.addUser(getNewUserDto());
		UserDto bookerDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemController.create(userDto.getId(), createItemRequest);
		CommentDto commentDto = CommentDto.builder()
				.text("comment")
				.item(item)
				.build();

		BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now())
				.end(LocalDateTime.now().plusSeconds(1))
				.build();

		try {
			BookingDto bookingDto = bookingService.addBooking(bookingRequestDto, bookerDto.getId());
			bookingDto = bookingService.approveBooking(bookingDto.getId(), userDto.getId(), true);
			Thread.sleep(2000);
			commentDto = itemController.createComment(bookerDto.getId(), item.getId(), commentDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(commentDto.getId());
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
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
