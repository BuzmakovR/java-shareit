package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {

	@NotNull(message = "Предмет бронирования не может быть пустым")
	private Long itemId;

	@NotNull(message = "Дата начала бронирования не может быть пустой")
	private LocalDateTime start;

	@NotNull(message = "Дата конца бронирования не может быть пустой")
	@Future
	private LocalDateTime end;
}
