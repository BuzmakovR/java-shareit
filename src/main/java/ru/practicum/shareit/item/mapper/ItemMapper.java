package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class ItemMapper {

	public static ItemDto toItemDto(Item item) {

		return ItemDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.isAvailable(item.getIsAvailable())
				.request(item.getRequest())
				.build();
	}

	public static ItemDto toItemDto(Item item, Collection<Comment> comments) {
		ItemDto itemDto = toItemDto(item);
		itemDto.setComments(
				comments.stream()
						.map(CommentMapper::toCommentDto)
						.toList());
		return itemDto;
	}

	public static Item fromItemDto(ItemDto itemDto) {
		return Item.builder()
				.id(itemDto.getId())
				.name(itemDto.getName())
				.description(itemDto.getDescription())
				.isAvailable(itemDto.getIsAvailable())
				.request(itemDto.getRequest())
				.build();
	}

	public static Item fromItemDto(ItemDto itemDto, User owner) {
		Item item = fromItemDto(itemDto);
		item.setOwner(owner);
		return item;
	}

	public static ItemOwnerDto toItemOwnerDto(Item item, Collection<Booking> bookings, Collection<Comment> comments) {
		ItemOwnerDto itemOwnerDto = ItemOwnerDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.isAvailable(item.getIsAvailable())
				.request(item.getRequest())
				.build();

		LocalDateTime localDateTime = LocalDateTime.now();

		bookings.stream()
				.filter(booking -> booking.getItem().getId().equals(item.getId()))
				.filter(booking -> booking.getStart().isBefore(localDateTime) && booking.getEnd().isAfter(localDateTime))
				.findFirst()
				.ifPresent(booking -> itemOwnerDto.setLastBooking(booking.getEnd()));

		bookings.stream()
				.filter(booking -> booking.getItem().getId().equals(item.getId()))
				.filter(booking -> booking.getStart().isAfter(localDateTime))
				.min(Comparator.comparing(Booking::getStart))
				.ifPresent(booking -> itemOwnerDto.setNextBooking(booking.getStart()));

		itemOwnerDto.setComments(comments.stream()
				.filter(comment -> Objects.equals(item.getId(), comment.getItem().getId()))
				.map(CommentMapper::toCommentDto)
				.toList());

		return itemOwnerDto;
	}

}
