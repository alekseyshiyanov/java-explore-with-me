package ru.practicum.ewm.mainservice.requests;

import java.util.Optional;

public enum RequestStatus {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Optional<RequestStatus> from(String stringState) {
        for (RequestStatus state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
