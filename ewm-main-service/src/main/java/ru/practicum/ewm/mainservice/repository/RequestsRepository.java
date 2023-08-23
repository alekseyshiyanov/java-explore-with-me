package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.mainservice.requests.RequestStatus;
import ru.practicum.ewm.mainservice.model.Requests;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<Requests, Long> {

    List<Requests> findAllByUserId(Long userId);

    Optional<Requests> findByIdAndUserId(Long requestId, Long userId);

    List<Requests> findAllByIdIsIn(List<Long> requestsIds);

    @Query("select r " +
            "from Requests r " +
            "where r.event.id = :eventId " +
            "and r.event.user.id = :userId")
    List<Requests> getRequestsByUserIdAndEventId(@Param("userId") Long userId,
                                                 @Param("eventId") Long eventId);


    @Modifying(clearAutomatically = true)
    @Query("update Requests r " +
            "set r.status = :status " +
            "where r.id in :requestIds"
    )
    int updateRequestsStatus(@Param("status") RequestStatus status,
                             @Param("requestIds") List<Long> requestIds);

    @Query("select count(r) " +
            "from Requests r " +
            "where r.status = :status " +
            "and r.id in :requestIds"
    )
    int checkRequestsStatus(@Param("status") RequestStatus status,
                            @Param("requestIds") List<Long> requestIds);
}
