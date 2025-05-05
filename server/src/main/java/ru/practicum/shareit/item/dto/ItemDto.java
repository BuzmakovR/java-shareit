package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemDto {

	private Long id;

	private String name;

	private String description;

	@JsonProperty("available")
	private Boolean isAvailable;

	private ItemRequestDto request;

	private LocalDateTime lastBooking;

	private LocalDateTime nextBooking;

	private Collection<CommentDto> comments;
}
