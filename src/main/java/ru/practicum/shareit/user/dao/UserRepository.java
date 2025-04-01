package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

public interface UserRepository {

	User get(Long id);

	User add(User user);

	User update(User newUser);

	void delete(Long id);

	boolean checkExistsOtherUserByParams(Long userId, String email);
}
