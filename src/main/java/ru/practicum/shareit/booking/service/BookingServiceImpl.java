package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NoRightsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final String NOT_FOUND_BOOKING_BY_ID = "Запрос бронирования не найден: ID = %d";
	private final String NOT_FOUND_USER_BY_ID = "Пользователь не найден: ID = %d";
	private final String NOT_FOUND_ITEM_BY_ID = "Элемент не найден: ID = %d";

	private final BookingRepository bookingRepository;

	private final UserRepository userRepository;

	private final ItemRepository itemRepository;

	@Override
	public BookingDto getBooking(Long bookingId, Long userId) {
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		Booking booking = optionalBooking.orElseThrow(() -> new NotFoundException(NOT_FOUND_BOOKING_BY_ID, bookingId));
		if (Objects.equals(booking.getBooker().getId(), userId) || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
			return BookingMapper.toBookingDto(booking);
		}
		throw new NotFoundException(NOT_FOUND_BOOKING_BY_ID, bookingId);
	}

	@Override
	public BookingDto addBooking(BookingRequestDto bookingRequestDto, Long ownerId) {
		User user = userRepository.findById(ownerId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, ownerId));
		Item item = itemRepository.findById(bookingRequestDto.getItemId())
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_ITEM_BY_ID, bookingRequestDto.getItemId()));
		Booking booking = BookingMapper.fromBookingDto(bookingRequestDto, user, item);
		bookingValidate(booking);
		booking.setStatus(BookingStatus.WAITING);
		return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
	}

	@Override
	public BookingDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
		Optional<Booking> optionalBooking = bookingRepository.findByIdAndItemOwnerId(bookingId, ownerId);
		Booking booking = optionalBooking.orElseThrow(NoRightsException::new);
		booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
		return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
	}

	@Override
	public Collection<BookingDto> getBookingsByUserIdAndState(Long userId, BookingState bookingState) {
		Collection<Booking> bookings = switch (bookingState) {
			case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
			case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
			case CURRENT ->
					bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
			case FUTURE ->
					bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
			case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case REJECTED ->
					bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
		};
		return bookings.stream()
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	@Override
	public Collection<BookingDto> getBookingsByItemOwnerIdAndState(Long userId, BookingState bookingState) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException(NOT_FOUND_USER_BY_ID, userId));
		Collection<Booking> bookings = switch (bookingState) {
			case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
			case PAST ->
					bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
			case CURRENT ->
					bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
			case FUTURE ->
					bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
			case WAITING ->
					bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
			case REJECTED ->
					bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
		};
		return bookings.stream()
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	private void bookingValidate(Booking booking) {
		if (booking.getStart().isAfter(booking.getEnd())
				|| booking.getStart().isEqual(booking.getEnd())) {
			throw new ConditionsNotMetException("Дата начала бронирования не может быть позже или равна дате окончания");
		}
		if (bookingRepository
				.findByItemIdAndStartBeforeAndEndAfter(booking.getId(), booking.getStart(), booking.getEnd())
				.isPresent()) {
			throw new ConditionsNotMetException("Данное время уже занято для бронирования элемента");
		}
		if (Boolean.FALSE.equals(booking.getItem().getIsAvailable())) {
			throw new ConditionsNotMetException("Элемент недоступен для бронирования");
		}
	}
}
