package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {

	private Long id;

	private LocalDateTime start;

	private LocalDateTime end;

	private Item item;

	private User booker;

	private BookingStatus status;
}
