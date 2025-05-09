package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemDto {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	@JsonProperty("available")
	private Boolean isAvailable;

	private ItemRequestDto request;

	private LocalDateTime lastBooking;

	private LocalDateTime nextBooking;

	Collection<CommentDto> comments;
}
