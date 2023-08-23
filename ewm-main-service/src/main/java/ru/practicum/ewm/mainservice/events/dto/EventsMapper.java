package ru.practicum.ewm.mainservice.events.dto;

import ru.practicum.ewm.mainservice.categories.dto.CategoryMapper;
import ru.practicum.ewm.mainservice.events.EventState;
import ru.practicum.ewm.mainservice.model.CompilationArray;
import ru.practicum.ewm.mainservice.model.Events;
import ru.practicum.ewm.mainservice.model.Locations;
import ru.practicum.ewm.mainservice.users.dto.UsersMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventsMapper {
    public static Events fromDto(NewEventDto inputDto) {
        return Events.builder()
                .id(null)
                .title(inputDto.getTitle())
                .annotation(inputDto.getAnnotation())
                .description(inputDto.getDescription())
                .eventDate(inputDto.getEventDate())
                .created(LocalDateTime.now())
                .publishedOn(null)
                .paid(inputDto.getPaid())
                .participantLimit(inputDto.getParticipantLimit())
                .requestModeration(inputDto.getRequestModeration())
                .state(EventState.PENDING)
                .latitude(inputDto.getLocation().getLat())
                .longitude(inputDto.getLocation().getLon())
                .confirmedRequests(0L)
                .views(0L)
                .category(null)
                .user(null)
                .build();
    }

    public static Events fromDto(UpdateEventDto inputDto) {
        var l = inputDto.getLocation();
        return Events.builder()
                .title(inputDto.getTitle())
                .annotation(inputDto.getAnnotation())
                .description(inputDto.getDescription())
                .eventDate(inputDto.getEventDate())
                .paid(inputDto.getPaid())
                .participantLimit(inputDto.getParticipantLimit())
                .requestModeration(inputDto.getRequestModeration())
                .latitude((l == null) ? null : l.getLat())
                .longitude((l == null) ? null : l.getLon())
                .build();
    }

    public static EventFullDto toFullDto(Events events) {
        return EventFullDto.builder()
                .id(events.getId())
                .annotation(events.getAnnotation())
                .category(CategoryMapper.toDto(events.getCategory()))
                .confirmedRequests(events.getConfirmedRequests())
                .createdOn(events.getCreated())
                .description(events.getDescription())
                .eventDate(events.getEventDate())
                .initiator(UsersMapper.toShortDto(events.getUser()))
                .location(new Locations(events.getLatitude(), events.getLongitude()))
                .paid(events.getPaid())
                .participantLimit(events.getParticipantLimit())
                .publishedOn(events.getPublishedOn())
                .requestModeration(events.getRequestModeration())
                .state(events.getState())
                .title(events.getTitle())
                .views(events.getViews())
                .build();
    }

    public static EventShortDto toShortDto(Events events) {
        return EventShortDto.builder()
                .id(events.getId())
                .annotation(events.getAnnotation())
                .category(CategoryMapper.toDto(events.getCategory()))
                .confirmedRequests(events.getConfirmedRequests())
                .eventDate(events.getEventDate())
                .initiator(UsersMapper.toShortDto(events.getUser()))
                .paid(events.getPaid())
                .title(events.getTitle())
                .views(events.getViews())
                .build();
    }

    public static EventShortDto toShortDto(CompilationArray compilationArray) {
        return toShortDto(compilationArray.getEvents());
    }

    public static List<EventShortDto> toShortDto(List<Events> eventList) {
        if (eventList == null) {
            return Collections.emptyList();
        }

        return eventList.stream()
                .map(EventsMapper::toShortDto)
                .collect(Collectors.toList());
    }

    public static List<EventFullDto> toFullDto(List<Events> eventList) {
        if (eventList == null) {
            return Collections.emptyList();
        }

        return eventList.stream()
                .map(EventsMapper::toFullDto)
                .collect(Collectors.toList());
    }
}
