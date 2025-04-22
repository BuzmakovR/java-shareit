package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

	BookingDto getBooking(Long bookingId, Long ownerId);

	BookingDto addBooking(BookingRequestDto bookingDto, Long userId);

	BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);

	Collection<BookingDto> getBookingsByUserIdAndState(Long userId, BookingState bookingState);

	Collection<BookingDto> getBookingsByItemOwnerIdAndState(Long userId, BookingState bookingState);
}
