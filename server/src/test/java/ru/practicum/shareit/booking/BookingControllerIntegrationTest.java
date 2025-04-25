package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingControllerIntegrationTest {

	private Long bookingCount = 1L;

	private Long userCount = 1L;

	@Autowired
	private final BookingController bookingController;

	@Autowired
	private final BookingService bookingService;

	@Autowired
	private final UserService userService;

	@Autowired
	private final ItemService itemService;

	@Test
	void createBooking() {
		UserDto userDto = userService.addUser(getNewUserDto());
		UserDto bookerDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemService.addItem(createItemRequest, userDto.getId());
		BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.build();

		BookingDto bookingDto = null;
		try {
			bookingDto = bookingController.create(bookerDto.getId(), bookingRequestDto);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(bookingDto);
		assertNotNull(bookingDto.getId());
	}

	@Test
	void updateBooking() {
		UserDto userDto = userService.addUser(getNewUserDto());
		UserDto bookerDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemService.addItem(createItemRequest, userDto.getId());
		BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.build();

		BookingDto bookingDto = bookingController.create(bookerDto.getId(), bookingRequestDto);
		try {
			bookingDto = bookingController.update(userDto.getId(), bookingDto.getId(), true);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(bookingDto);
		assertNotNull(bookingDto.getStatus());
		assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
	}

	@Test
	void getBookings() {
		UserDto userDto = userService.addUser(getNewUserDto());
		UserDto bookerDto = userService.addUser(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemDto item = itemService.addItem(createItemRequest, userDto.getId());
		BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
				.itemId(item.getId())
				.start(LocalDateTime.now().plusSeconds(1))
				.end(LocalDateTime.now().plusSeconds(2))
				.build();

		BookingDto bookingDto = bookingController.create(bookerDto.getId(), bookingRequestDto);
		bookingDto = bookingController.update(userDto.getId(), bookingDto.getId(), true);

		BookingDto bookingDtoGet = null;
		Collection<BookingDto> bookingByUser = null;
		Collection<BookingDto> bookingByItemOwner = null;
		try {
			bookingDtoGet = bookingController.get(bookerDto.getId(), bookingDto.getId());
			bookingByUser = bookingController.getBookingsByUser(bookerDto.getId(), BookingState.ALL);
			bookingByItemOwner = bookingController.getBookingsByItemOwner(userDto.getId(), BookingState.ALL);
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
		assertNotNull(bookingDtoGet);
		assertNotNull(bookingByUser);
		assertNotNull(bookingByItemOwner);
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
