package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

	private Long id;

	private String name;

	@Email
	@NotBlank(message = "Email пользователя должен быть заполнен")
	private String email;

}
