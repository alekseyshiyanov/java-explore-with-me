package ru.practicum.ewm.mainservice.users.dto;

import ru.practicum.ewm.mainservice.model.Users;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UsersMapper {
    public static List<UserDto> toDto(List<Users> usersList) {
        if (usersList == null) {
            return Collections.emptyList();
        }

        return usersList.stream()
                .map(UsersMapper::toDto)
                .collect(Collectors.toList());
    }

    public static UserDto toDto(Users users) {
        if (users == null) {
            return null;
        }

        return UserDto.builder()
                .id(users.getId())
                .name(users.getName())
                .email(users.getEmail())
                .build();
    }

    public static List<UserShortDto> toShortDto(List<Users> usersList) {
        if (usersList == null) {
            return Collections.emptyList();
        }

        return usersList.stream()
                .map(UsersMapper::toShortDto)
                .collect(Collectors.toList());
    }

    public static UserShortDto toShortDto(Users users) {
        if (users == null) {
            return null;
        }

        return UserShortDto.builder()
                .id(users.getId())
                .name(users.getName())
                .build();
    }

    public static Users fromDto(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return Users.builder()
                .id(null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
