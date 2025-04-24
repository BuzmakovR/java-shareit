package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemForRequestDto {

	private Long id;

	@NotBlank
	private String name;

	private Long ownerId;
}
