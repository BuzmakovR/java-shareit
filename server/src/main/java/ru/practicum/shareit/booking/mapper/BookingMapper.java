package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.temporal.ChronoUnit;

public class BookingMapper {

	public static BookingDto toBookingDto(Booking booking) {
		return BookingDto.builder()
				.id(booking.getId())
				.item(ItemMapper.toItemDto(booking.getItem()))
				.booker(UserMapper.toUserDto(booking.getBooker()))
				.status(booking.getStatus())
				.start(booking.getStart())
				.end(booking.getEnd())
				.build();
	}

	public static Booking fromBookingDto(BookingRequestDto bookingRequestDto, User booker, Item item) {
		return Booking.builder()
				.item(item)
				.booker(booker)
				.start(bookingRequestDto.getStart().truncatedTo(ChronoUnit.SECONDS))
				.end(bookingRequestDto.getEnd().truncatedTo(ChronoUnit.SECONDS))
				.build();
	}
}
