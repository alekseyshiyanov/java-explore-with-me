package ru.practicum.ewm.mainservice.ranking;

import java.util.Optional;

public enum LikeState {
    DISLIKE,
    LIKE;

    public static Optional<LikeState> from(String stringState) {
        for (LikeState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
