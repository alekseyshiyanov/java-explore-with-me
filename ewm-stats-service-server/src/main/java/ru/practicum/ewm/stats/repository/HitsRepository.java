package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.model.Hits;
import ru.practicum.statsclient.stats.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<Hits, Long> {

    @Query("select new ru.practicum.statsclient.stats.StatsDto (" +
            "h.app.name, " +
            "h.uri, " +
            "count (h.app.name) as hits_count) " +
            "from Hits h " +
            "where h.created between :start and :end " +
            "group by h.app.name, h.uri " +
            "order by hits_count DESC")
    List<StatsDto> findAllByCreatedBetweenTimesNonUniqueIpAddressUrisIsEmpty(@Param("start") LocalDateTime start,
                                                                             @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.statsclient.stats.StatsDto (" +
            "h.app.name, " +
            "h.uri, " +
            "count (distinct (h.ipAddress)) as hits_count) " +
            "from Hits h " +
            "where h.created between :start and :end " +
            "group by h.app.name, h.uri " +
            "order by hits_count DESC")
    List<StatsDto> findAllByCreatedBetweenTimesUniqueIpAddressUrisIsEmpty(@Param("start") LocalDateTime start,
                                                                          @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.statsclient.stats.StatsDto (" +
            "h.app.name, " +
            "h.uri, " +
            "count (h.ipAddress) as hits_count) " +
            "from Hits h " +
            "where (h.created between :start and :end) " +
            "and h.uri in :urisList " +
            "group by h.app.name, h.uri " +
            "order by hits_count DESC")
    List<StatsDto> findAllByCreatedBetweenTimesNonUniqueIpAddressUrisNotEmpty(@Param("start") LocalDateTime start,
                                                                              @Param("end") LocalDateTime end,
                                                                              @Param("urisList") List<String> uris);


    @Query("select new ru.practicum.statsclient.stats.StatsDto (" +
            "h.app.name, " +
            "h.uri, " +
            "count (distinct (h.ipAddress)) as hits_count) " +
            "from Hits h " +
            "where (h.created between :start and :end) " +
            "and h.uri in :urisList " +
            "group by h.app.name, h.uri " +
            "order by hits_count DESC")
    List<StatsDto> findAllByCreatedBetweenTimesUniqueIpAddressUrisNotEmpty(@Param("start") LocalDateTime start,
                                                                           @Param("end") LocalDateTime end,
                                                                           @Param("urisList") List<String> uris);

    @Query("select count(distinct(h.uri)) " +
            "from Hits h " +
            "where h.uri in :urisList "
        )
    Long checkUrisListExist(@Param("urisList") List<String> uris);
}
