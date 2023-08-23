package ru.practicum.ewm.mainservice.events;

import java.util.Optional;

public enum EventSortTypes {
    ID,
    EVENT_DATE,
    VIEWS;

    public static Optional<EventSortTypes> from(String stringState) {
        for (EventSortTypes state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
