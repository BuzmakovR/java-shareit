package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@JsonProperty("available")
	private Boolean isAvailable;

	private ItemRequest request;
}
