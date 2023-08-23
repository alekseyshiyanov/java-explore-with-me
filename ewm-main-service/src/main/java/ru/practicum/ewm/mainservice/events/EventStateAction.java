package ru.practicum.ewm.mainservice.events;

import java.util.Optional;

public enum EventStateAction {
    NONE_EVENT,
    PUBLISH_EVENT,
    REJECT_EVENT;

    public static Optional<EventStateAction> from(String stringState) {
        for (EventStateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
