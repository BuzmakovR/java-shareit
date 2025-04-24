package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;

	@GetMapping("/{bookingId}")
	public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
						  @PathVariable Long bookingId) {
		return bookingService.getBooking(bookingId, userId);
	}

	@GetMapping
	public Collection<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
													@RequestParam(required = false, defaultValue = "all") BookingState state) {
		return bookingService.getBookingsByUserIdAndState(userId, state);
	}

	@GetMapping("/owner")
	public Collection<BookingDto> getBookingsByItemOwner(@RequestHeader("X-Sharer-User-Id") long userId,
														 @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
		return bookingService.getBookingsByItemOwnerIdAndState(userId, state);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
							 @RequestBody BookingRequestDto bookingRequestDto) {
		return bookingService.addBooking(bookingRequestDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
							 @PathVariable Long bookingId,
							 @RequestParam Boolean approved) {
		return bookingService.approveBooking(bookingId, userId, approved);
	}
}

