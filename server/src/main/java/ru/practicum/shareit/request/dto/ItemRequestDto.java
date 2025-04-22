package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
public class ItemRequestDto {

	private Long id;

	private String description;

	private UserDto requestor;

	private LocalDateTime created;

	private Collection<ItemForRequestDto> items;
}
