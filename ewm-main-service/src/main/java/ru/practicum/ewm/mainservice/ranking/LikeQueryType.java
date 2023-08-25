package ru.practicum.ewm.mainservice.ranking;

import java.util.Optional;

public enum LikeQueryType {
    ALL,
    POSITIVE,
    NEGATIVE;

    public static Optional<LikeQueryType> from(String stringState) {
        for (LikeQueryType state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
