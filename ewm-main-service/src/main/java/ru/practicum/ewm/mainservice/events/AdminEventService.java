package ru.practicum.ewm.mainservice.events;

import ru.practicum.ewm.mainservice.events.dto.UpdateEventDto;
import ru.practicum.ewm.mainservice.model.Events;

import java.util.List;

public interface AdminEventService {
    List<Events> getFilteredEventList(List<Long> usersList, List<String> statesList, List<Long> categoriesList,
                                            String rangeStart, String rangeEnd, int from, int size);

    Events updateEventDataAndStatus(Long eventId, UpdateEventDto eventDto);
}
