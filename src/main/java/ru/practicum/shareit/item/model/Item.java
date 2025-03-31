package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	@JsonProperty("available")
	private Boolean isAvailable;

	private Long ownerId;

	private ItemRequest request;
}
