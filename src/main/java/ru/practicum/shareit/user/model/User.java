package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {

	private Long id;

	private String name;

	@Email
	@NotBlank(message = "Email пользователя должен быть заполнен")
	private String email;
}
