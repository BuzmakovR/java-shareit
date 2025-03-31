package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {

	User get(Long id);

	User add(User user);

	User update(User newUser);

	User delete(Long id);

	boolean checkExistsOtherUserByParams(Long userId, String email);
}
