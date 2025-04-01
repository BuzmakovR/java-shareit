package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingMapper {

	public static BookingDto toBookingDto(Booking booking) {
		return BookingDto.builder()
				.item(booking.getItem())
				.booker(booking.getBooker())
				.status(booking.getStatus())
				.start(booking.getStart())
				.end(booking.getEnd())
				.build();
	}
}
