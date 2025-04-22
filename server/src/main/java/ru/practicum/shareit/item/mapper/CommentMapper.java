package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

	public static CommentDto toCommentDto(Comment comment) {
		return CommentDto.builder()
				.id(comment.getId())
				.text(comment.getText())
				.item(ItemMapper.toItemDto(comment.getItem()))
				.authorName(comment.getAuthor().getName())
				.created(comment.getCreated())
				.build();
	}

	public static Comment fromCommentDto(CommentDto commentDto) {
		return Comment.builder()
				.id(commentDto.getId())
				.text(commentDto.getText())
				.item(commentDto.getItem() == null ? null : ItemMapper.fromItemDto(commentDto.getItem()))
				.created(commentDto.getCreated())
				.build();
	}
}
