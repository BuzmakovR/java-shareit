package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

	@InjectMocks
	private ItemServiceImpl itemService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemRequestRepository itemRequestRepository;

	@Mock
	private CommentRepository commentRepository;

	private Long userCount = 1L;

	private Long itemCount = 1L;

	@Test
	void addItem() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);
		CreateItemRequest createItemRequest = createItemRequest();
		Item item = Item.builder()
				.id(itemCount++)
				.name(createItemRequest().getName())
				.description(createItemRequest().getDescription())
				.isAvailable(createItemRequest.getIsAvailable())
				.build();

		when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		when(itemRepository.saveAndFlush(ItemMapper.fromCreateItemRequest(createItemRequest, user, null)))
				.thenReturn(item);

		ItemDto itemDtoCreated = itemService.addItem(createItemRequest, user.getId());
		assertNotNull(itemDtoCreated);
		assertEquals(createItemRequest.getName(), itemDtoCreated.getName());
		assertEquals(createItemRequest.getDescription(), itemDtoCreated.getDescription());
		verify(userRepository, times(1)).findById(anyLong());
		verify(itemRepository, times(1)).saveAndFlush(any());
	}

	@Test
	void addItemWithRequest() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);
		User requestor = UserMapper.fromUserDto(getNewUserDto());
		CreateItemRequest createItemRequest = createItemRequest();
		ItemRequest itemRequest = ItemRequest.builder()
				.id(1L)
				.created(LocalDateTime.now())
				.requestor(requestor)
				.description("test")
				.build();
		createItemRequest.setRequestId(itemRequest.getId());
		Item item = Item.builder()
				.id(itemCount++)
				.name(createItemRequest().getName())
				.description(createItemRequest().getDescription())
				.isAvailable(createItemRequest.getIsAvailable())
				.request(itemRequest)
				.build();

		when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		when(itemRequestRepository.findById(anyLong()))
				.thenReturn(Optional.of(itemRequest));

		when(itemRepository.saveAndFlush(ItemMapper.fromCreateItemRequest(createItemRequest, user, itemRequest)))
				.thenReturn(item);

		ItemDto itemDtoCreated = itemService.addItem(createItemRequest, user.getId());
		assertNotNull(itemDtoCreated);
		assertEquals(createItemRequest.getName(), itemDtoCreated.getName());
		assertEquals(createItemRequest.getDescription(), itemDtoCreated.getDescription());
		verify(userRepository, times(1)).findById(anyLong());
		verify(itemRequestRepository, times(1)).findById(anyLong());
		verify(itemRepository, times(1)).saveAndFlush(any());
	}

	@Test
	void getItem() {
		User user = UserMapper.fromUserDto(getNewUserDto());
		Item item = Item.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();
		Comment comment = Comment.builder()
				.text("comment")
				.author(UserMapper.fromUserDto(getNewUserDto()))
				.item(item)
				.build();

		when(userRepository.findById(user.getId()))
				.thenReturn(Optional.of(user));
		when(itemRepository.findById(item.getId()))
				.thenReturn(Optional.of(item));
		when(commentRepository.findAllByItemIdIn(Set.of(item.getId())))
				.thenReturn(List.of(comment));

		ItemDto itemDto = itemService.getItem(item.getId(), user.getId());

		assertNotNull(itemDto);
		assertEquals(item.getId(), itemDto.getId());
		assertEquals(item.getName(), itemDto.getName());
		assertEquals(item.getDescription(), itemDto.getDescription());
		assertEquals(1, itemDto.getComments().size());
		assertEquals(CommentMapper.toCommentDto(comment), itemDto.getComments().stream().toList().getFirst());
		verify(itemRepository, times(1)).findById(item.getId());
		verify(commentRepository, times(1)).findAllByItemIdIn(Set.of(item.getId()));
	}

	@Test
	void updateItem() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);
		Item item = Item.builder()
				.id(itemCount++)
				.name("name")
				.description("description")
				.isAvailable(true)
				.build();
		UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
				.name(item.getName() + "_update")
				.description(item.getDescription() + "_update")
				.isAvailable(false)
				.build();
		Item itemUpdated = Item.builder()
				.id(item.getId())
				.name(updateItemRequest.getName())
				.description(updateItemRequest.getDescription())
				.isAvailable(updateItemRequest.getIsAvailable())
				.build();

		when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong()))
				.thenReturn(Optional.of(item));
		when(itemRepository.saveAndFlush(item))
				.thenReturn(itemUpdated);

		ItemDto itemDtoCreated = itemService.updateItem(item.getId(), updateItemRequest, user.getId());
		assertNotNull(itemDtoCreated);
		assertEquals(itemUpdated.getName(), itemDtoCreated.getName());
		assertEquals(itemUpdated.getDescription(), itemDtoCreated.getDescription());
		assertEquals(itemUpdated.getIsAvailable(), itemDtoCreated.getIsAvailable());
		verify(itemRepository, times(1)).findByIdAndOwnerId(anyLong(), anyLong());
		verify(itemRepository, times(1)).saveAndFlush(item);
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
	}

	private CreateItemRequest createItemRequest() {
		return CreateItemRequest.builder()
				.name("name")
				.description("desc")
				.isAvailable(true)
				.build();
	}
}
