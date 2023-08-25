package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Likes;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findLikesByEventIdAndUserId(Long eventId, Long userId);
}
