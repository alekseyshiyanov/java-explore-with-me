package ru.practicum.ewm.mainservice.events;

import ru.practicum.ewm.mainservice.events.dto.*;
import ru.practicum.ewm.mainservice.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventsService {
    EventFullDto createEvent(Long userId, NewEventDto eventDto);

    List<EventShortDto> getEventsListByUser(Long userId, int from, int size);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventDto eventDto);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventByUserRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto dto);
}
