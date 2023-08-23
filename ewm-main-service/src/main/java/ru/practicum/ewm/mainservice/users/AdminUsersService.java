package ru.practicum.ewm.mainservice.users;

import ru.practicum.ewm.mainservice.users.dto.UserDto;

import java.util.List;

public interface AdminUsersService {
    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);
}
