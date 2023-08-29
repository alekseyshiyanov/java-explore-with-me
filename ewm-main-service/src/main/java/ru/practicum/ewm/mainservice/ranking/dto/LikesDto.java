package ru.practicum.ewm.mainservice.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.events.dto.EventShortDto;
import ru.practicum.ewm.mainservice.ranking.LikeState;
import ru.practicum.ewm.mainservice.users.dto.UserShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikesDto {
    private Long id;

    private UserShortDto user;

    private EventShortDto event;

    private LikeState grade;
}
