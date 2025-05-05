package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateItemRequest {

	private Long id;

	private String name;

	private String description;

	@JsonProperty("available")
	private Boolean isAvailable;

	private Long requestId;
}
