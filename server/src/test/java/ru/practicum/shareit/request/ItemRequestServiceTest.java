package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

	@InjectMocks
	private ItemRequestServiceImpl itemRequestService;

	@Mock
	private ItemRequestRepository itemRequestRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ItemRepository itemRepository;

	private Long itemRequestCount = 1L;

	private Long userCount = 1L;

	@Test
	void getItemRequest() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);

		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);
		when(itemRequestRepository.findById(itemRequestDto.getId()))
				.thenReturn(Optional.of(itemRequest));

		when(itemRepository.findAllByRequestIdIn(Set.of(itemRequestDto.getId())))
				.thenReturn(List.of());

		ItemRequestDto itemRequestDtoResult = itemRequestService.getItemRequest(itemRequestDto.getId());

		assertNotNull(itemRequestDtoResult);
		assertEquals(itemRequestDto.getId(), itemRequestDtoResult.getId());
		assertEquals(itemRequestDto.getDescription(), itemRequestDtoResult.getDescription());
		assertEquals(itemRequestDto.getRequestor(), itemRequestDtoResult.getRequestor());
		verify(itemRequestRepository, times(1)).findById(itemRequestDto.getId());
	}

	@Test
	void getNotExistsItemRequest() {
		Long notExistsItemRequestId = -1L;

		when(itemRequestRepository.findById(notExistsItemRequestId))
				.thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(notExistsItemRequestId));
		verify(itemRequestRepository, times(1)).findById(notExistsItemRequestId);
	}

	@Test
	void getItemRequests() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);

		UserDto otherUserDto = getNewUserDto();
		User otherUser = UserMapper.fromUserDto(otherUserDto);

		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);
		when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(itemRequestDto.getId()))
				.thenReturn(List.of(itemRequest));


		Item item = Item.builder()
				.id(1L)
				.name("name")
				.description("desc")
				.owner(otherUser)
				.isAvailable(true)
				.request(itemRequest)
				.build();

		when(itemRepository.findAllByRequestIdIn(Set.of(itemRequestDto.getId())))
				.thenReturn(List.of(item));

		List<ItemRequestDto> itemRequestDtoResult = itemRequestService.getItemRequests(user.getId()).stream().toList();

		assertNotNull(itemRequestDtoResult);
		assertEquals(1, itemRequestDtoResult.size());
		assertEquals(itemRequestDto.getId(), itemRequestDtoResult.getFirst().getId());
		assertEquals(itemRequestDto.getDescription(), itemRequestDtoResult.getFirst().getDescription());
		assertEquals(itemRequestDto.getRequestor(), itemRequestDtoResult.getFirst().getRequestor());
		assertEquals(1, itemRequestDtoResult.getFirst().getItems().size());
		assertEquals(ItemMapper.toItemForRequestDto(item), itemRequestDtoResult.getFirst().getItems().stream().toList().getFirst());
		verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(itemRequestDto.getId());
		verify(itemRepository, times(1)).findAllByRequestIdIn(any());
	}

	@Test
	void getItemRequestsFromOtherUsers() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);

		UserDto otherUserDto = getNewUserDto();
		User otherUser = UserMapper.fromUserDto(otherUserDto);

		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);

		ItemRequestDto itemRequestDtoOtherUser = getNewItemRequestDto(otherUserDto);
		ItemRequest itemRequestOtherUser = ItemRequestMapper.fromItemRequestDto(itemRequestDtoOtherUser, otherUser);

		when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(user.getId()))
				.thenReturn(List.of(itemRequestOtherUser));

		when(itemRepository.findAllByRequestIdIn(Set.of(itemRequestOtherUser.getId())))
				.thenReturn(List.of());

		List<ItemRequestDto> itemRequestDtoResult = itemRequestService.getItemRequestsFromOtherUsers(user.getId()).stream().toList();

		assertNotNull(itemRequestDtoResult);
		assertEquals(1, itemRequestDtoResult.size());
		assertEquals(itemRequestDtoOtherUser.getId(), itemRequestDtoResult.getFirst().getId());
		assertEquals(itemRequestDtoOtherUser.getDescription(), itemRequestDtoResult.getFirst().getDescription());
		assertEquals(itemRequestDtoOtherUser.getRequestor(), itemRequestDtoResult.getFirst().getRequestor());
		verify(itemRequestRepository, times(1)).findAllByRequestorIdNotOrderByCreatedDesc(user.getId());
		verify(itemRepository, times(1)).findAllByRequestIdIn(any());
	}

	@Test
	void addItemRequest() {
		UserDto userDto = getNewUserDto();
		User user = UserMapper.fromUserDto(userDto);
		when(userRepository.findById(anyLong()))
				.thenReturn(Optional.of(user));

		ItemRequestDto itemRequestDto = getNewItemRequestDto(userDto);
		ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);
		when(itemRequestRepository.saveAndFlush(any()))
				.thenReturn(itemRequest);

		ItemRequestDto itemRequestDtoResult = itemRequestService.addItemRequest(itemRequestDto, user.getId());
		assertNotNull(itemRequestDtoResult);
		assertEquals(itemRequestDto.getId(), itemRequestDtoResult.getId());
		assertEquals(itemRequestDto.getDescription(), itemRequestDtoResult.getDescription());
		assertEquals(itemRequestDto.getRequestor(), itemRequestDtoResult.getRequestor());

		verify(userRepository, times(1)).findById(anyLong());
		verify(itemRequestRepository, times(1)).saveAndFlush(any());
	}

	private UserDto getNewUserDto() {
		userCount++;
		return UserDto.builder()
				.id(userCount)
				.name("user_" + userCount)
				.email("user_" + userCount + "@mail.ru")
				.build();
	}

	private ItemRequestDto getNewItemRequestDto(UserDto userDto) {
		itemRequestCount++;
		return ItemRequestDto.builder()
				.id(itemRequestCount)
				.description("text_" + itemRequestCount)
				.requestor(userDto)
				.build();
	}
}
