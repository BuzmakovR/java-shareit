package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

	@NotNull(message = "Предмет бронирования не может быть пустым")
	private long itemId;

	@NotNull(message = "Дата начала бронирования не может быть пустой")
	@FutureOrPresent
	private LocalDateTime start;

	@NotNull(message = "Дата конца бронирования не может быть пустой")
	@Future
	private LocalDateTime end;
}
