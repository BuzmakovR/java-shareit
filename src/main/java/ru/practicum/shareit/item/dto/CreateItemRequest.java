package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateItemRequest {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	@JsonProperty("available")
	private Boolean isAvailable;

	private Long requestId;
}
