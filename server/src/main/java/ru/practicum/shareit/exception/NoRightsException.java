package ru.practicum.shareit.exception;

public class NoRightsException extends RuntimeException {

	public NoRightsException() {
		super("Недостаточно прав для выполнения данного действия");
	}

}
