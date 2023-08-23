package ru.practicum.ewm.mainservice.statistics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.statsclient.client.StatsClient;
import ru.practicum.statsclient.hits.HitDto;
import ru.practicum.statsclient.stats.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatsClient client;

    @Value("${spring.application.name}")
    private String applicationName;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void sendStatisticData(String ipAddress, String uri, LocalDateTime timestamp) {
        var hitDto = HitDto.builder()
                .ip(ipAddress)
                .app(applicationName)
                .uri(uri)
                .timestamp(timestamp)
                .build();

        client.createHitRecord(hitDto);
    }

    public Long getEventViewFromUniqueIpAddress(Long eventId) {
        List<String> urisList = Arrays.asList("/events/" + eventId);
        var ret = client.getStats(LocalDateTime.now().minusYears(100L).format(dtf), LocalDateTime.now().plusYears(100L).format(dtf), urisList, true);
        if ((ret.getStatusCode() == HttpStatus.OK) && (ret.hasBody())) {
            ObjectMapper om = new ObjectMapper();
            try {
                String source = om.writeValueAsString(ret.getBody());
                List<StatsDto> dto = om.readValue(source, new TypeReference<>() {});
                if (dto.isEmpty()) {
                    return 0L;
                }
                return dto.get(0).getHits();
            } catch (Exception e) {
                log.error("Ошибка в данных от сервера статистики. ErrorMessage: {}", e.getMessage());
                return null;
            }
        }
        log.error("Нет ответа от сервера статистики. Код: {}. HasBody: {}", ret.getStatusCode(), ret.hasBody());
        return null;
    }
}
