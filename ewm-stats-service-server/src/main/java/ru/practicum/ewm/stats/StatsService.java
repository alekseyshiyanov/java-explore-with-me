package ru.practicum.ewm.stats;

import ru.practicum.statsclient.hits.HitDto;
import ru.practicum.statsclient.stats.StatsDto;

import java.util.List;

public interface StatsService {

    HitDto createHitRecord(HitDto hitDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
}