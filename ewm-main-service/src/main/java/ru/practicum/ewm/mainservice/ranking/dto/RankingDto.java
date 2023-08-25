package ru.practicum.ewm.mainservice.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.mainservice.events.dto.EventShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingDto {
    private EventShortDto event;

    private Double ranking;

    private Long likes;

    private Long positive;
}
