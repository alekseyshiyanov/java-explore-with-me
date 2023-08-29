package ru.practicum.ewm.mainservice.ranking.dto;

import ru.practicum.ewm.mainservice.events.dto.EventsMapper;
import ru.practicum.ewm.mainservice.model.Likes;
import ru.practicum.ewm.mainservice.users.dto.UsersMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LikesMapper {
    public static LikesDto toDto(Likes likes) {
        return LikesDto.builder()
                .id(likes.getId())
                .user(UsersMapper.toShortDto(likes.getUser()))
                .event(EventsMapper.toShortDto(likes.getEvent()))
                .grade(likes.getGrade())
                .build();
    }

    public static List<LikesDto> toDto(List<Likes> likes) {
        if (likes == null || likes.isEmpty()) {
            return Collections.emptyList();
        }

        return likes.stream()
                .map(LikesMapper::toDto)
                .collect(Collectors.toList());
    }
}
