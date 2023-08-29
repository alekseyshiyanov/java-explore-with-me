package ru.practicum.ewm.mainservice.ranking;

import ru.practicum.ewm.mainservice.ranking.dto.LikesDto;

import java.util.List;

public interface LikesService {
    LikesDto evaluateEvent(Long eventId, Long userId, String grade);

    LikesDto updateEventEvaluate(Long eventId, Long userId, String grade);

    List<LikesDto> getEventLikes(Long eventId, String queryType, int from, int size);

    List<LikesDto> getUserLikes(Long userId, String queryType, int from, int size);

    void deleteEventEvaluate(Long eventId, Long userId);
}
