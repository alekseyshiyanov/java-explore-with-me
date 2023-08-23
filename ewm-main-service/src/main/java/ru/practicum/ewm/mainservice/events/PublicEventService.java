package ru.practicum.ewm.mainservice.events;

import ru.practicum.ewm.mainservice.model.Events;

import java.util.List;

public interface PublicEventService {
    List<Events> getFilteredEventList(String searchString, List<Long> categoriesList, Boolean paid,
                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                      String sort, int from, int size);

    Events getPublishedEventsById(Long eventId);
}
