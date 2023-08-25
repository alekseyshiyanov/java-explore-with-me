package ru.practicum.ewm.mainservice.ranking;

import ru.practicum.ewm.mainservice.ranking.dto.RankingDto;

import java.util.List;

public interface RankingService {
    RankingDto getEventRanking(Long eventId);

    List<RankingDto> getSortedRanking(String sort, int from, int size);
}
