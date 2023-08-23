package ru.practicum.ewm.mainservice.events;

import java.util.Optional;

public enum UpdateEventStateAction {
    NONE_EVENT,
    SEND_TO_REVIEW,
    CANCEL_REVIEW;

    public static Optional<UpdateEventStateAction> from(String stringState) {
        for (UpdateEventStateAction state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
