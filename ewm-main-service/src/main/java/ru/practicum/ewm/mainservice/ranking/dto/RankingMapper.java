package ru.practicum.ewm.mainservice.ranking.dto;

import ru.practicum.ewm.mainservice.events.dto.EventsMapper;
import ru.practicum.ewm.mainservice.model.Events;
import ru.practicum.ewm.mainservice.model.Ranking;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RankingMapper {
    public static RankingDto toDto(Ranking ranking) {
        return RankingDto.builder()
                .event(EventsMapper.toShortDto(ranking.getEvent()))
                .ranking(ranking.getRanking())
                .likes(ranking.getLikes())
                .positive(ranking.getPositive())
                .build();
    }

    public static List<RankingDto> toDto(List<Ranking> rankingList) {
        if (rankingList == null || rankingList.isEmpty()) {
            return Collections.emptyList();
        }

        return rankingList.stream()
                .map(RankingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Ranking fromQuery(Object[] queryReturn, Long minimumVotes) {
        Long eventLikesCount = (queryReturn[0] == null) ? 0L : (Long) queryReturn[0];
        Long positiveLikesCount = (queryReturn[1] == null) ? 0L : (Long) queryReturn[1];
        Double rating = (queryReturn[2] == null || eventLikesCount < minimumVotes) ? 0.0 : (Double) queryReturn[2];
        Events event = (Events) queryReturn[3];

        return Ranking.builder()
                .event(event)
                .likes(eventLikesCount)
                .positive(positiveLikesCount)
                .ranking(rating)
                .build();
    }

    public static List<Ranking> fromQuery(List<Object[]> queryListReturn, Long minimumVotes) {
        if (queryListReturn == null || queryListReturn.isEmpty()) {
            return Collections.emptyList();
        }
        return queryListReturn.stream()
                .map(r -> fromQuery(r, minimumVotes))
                .collect(Collectors.toList());
    }
}
