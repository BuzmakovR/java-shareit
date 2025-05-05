package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NoRightsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handlerNotFoundException(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handlerValidationException(final ValidationException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handlerConflictException(final ConflictException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handlerNotRightsException(final NoRightsException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handlerConditionsNotMetException(final ConditionsNotMetException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
		return new ErrorResponse(
				Arrays.stream(Objects.requireNonNull(e.getDetailMessageArguments()))
						.map(String::valueOf)
						.filter(s -> !(s.isEmpty() || s.isBlank()))
						.collect(Collectors.joining(";")));
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handlerInternalServerException(final Throwable e) {
		return new ErrorResponse("Произошла непредвиденная ошибка");
	}
}
