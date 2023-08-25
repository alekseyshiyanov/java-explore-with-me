package ru.practicum.ewm.mainservice.ranking;

import java.util.Optional;

public enum RankingSortType {
    NONE,
    RATING;

    public static Optional<RankingSortType> from(String stringState) {
        for (RankingSortType state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
