package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.model.Hits;
import ru.practicum.statsclient.hits.HitDto;

public class HitsMapper {
    public static Hits fromDto(HitDto hitDto) {
        return Hits.builder()
                .id(null)
                .uri(hitDto.getUri())
                .app(null)
                .ipAddress(hitDto.getIp())
                .created(hitDto.getTimestamp())
                .build();
    }
    public static HitDto toDto(Hits hit) {
        return HitDto.builder()
                .app(hit.getApp().getName())
                .uri(hit.getUri())
                .ip(hit.getIpAddress())
                .timestamp(hit.getCreated())
                .build();
    }
}
