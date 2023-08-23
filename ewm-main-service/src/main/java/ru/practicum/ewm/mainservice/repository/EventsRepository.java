package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.events.EventState;
import ru.practicum.ewm.mainservice.model.Events;

import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<Events, Long> {
    Optional<Events> findByIdAndUserId(Long eventId, Long userId);

    List<Events> getEventsByIdIn(List<Long> idsList);

    @Modifying(clearAutomatically = true)
    @Query("update Events e " +
            "set e.confirmedRequests = :newValue " +
            "where e.id = :eventId ")
    int setConfirmedRequests(@Param("eventId") Long eventId,
                                @Param("newValue") Long newValue);

    @Modifying(clearAutomatically = true)
    @Query("update Events e " +
            "set e.confirmedRequests = e.confirmedRequests + :adder " +
            "where e.id = :eventId")
    int increaseConfirmedRequests(@Param("eventId") Long eventId, @Param("adder") Long adder);
    
    @Modifying(clearAutomatically = true)
    @Query("update Events e " +
            "set e.confirmedRequests = e.confirmedRequests - :adder " +
            "where e.id = :eventId and e.confirmedRequests >= :adder ")
    int decreaseConfirmedRequests(@Param("eventId") Long eventId, @Param("adder") Long adder);

    Optional<Events> findByIdAndState(Long eventId, EventState state);
}
