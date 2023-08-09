package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.model.Apps;
import ru.practicum.ewm.stats.model.Hits;
import ru.practicum.ewm.stats.repository.AppsRepository;
import ru.practicum.ewm.stats.repository.HitsRepository;
import ru.practicum.statsclient.exceptions.ApiErrorException;
import ru.practicum.statsclient.hits.HitDto;
import ru.practicum.statsclient.stats.StatsDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final HitsRepository hitsRepository;
    private final AppsRepository appsRepository;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public HitDto createHitRecord(HitDto hitDto) {
        Hits newHit = HitsMapper.fromDto(hitDto);
        newHit.setApp(checkApps(hitDto.getApp()));

        var retHit = hitsRepository.save(newHit);

        return HitsMapper.toDto(retHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start.trim(), dtf);
        LocalDateTime endTime = LocalDateTime.parse(end.trim(), dtf);

        if((uris == null) || (uris.isEmpty())) {
            return getStatsByTime(startTime, endTime, unique);
        }

        if (hitsRepository.checkUrisListExist(uris) != uris.size()) {
            throw sendErrorMessage(HttpStatus.BAD_REQUEST, "Одно из имен сервиса отсутствует в базе данных");
        }

        if (unique) {
            return hitsRepository.findAllByCreatedBetweenTimesUniqueIpAddressUrisNotEmpty(startTime, endTime, uris);
        }

        return hitsRepository.findAllByCreatedBetweenTimesNonUniqueIpAddressUrisNotEmpty(startTime, endTime, uris);
    }

    private List<StatsDto> getStatsByTime(LocalDateTime startTime, LocalDateTime endTime, Boolean unique) {
        if (unique) {
            return hitsRepository.findAllByCreatedBetweenTimesUniqueIpAddressUrisIsEmpty(startTime, endTime);
        }
        return hitsRepository.findAllByCreatedBetweenTimesNonUniqueIpAddressUrisIsEmpty(startTime, endTime);
    }

    private Apps checkApps(String name) {
        var app = appsRepository.getAppsByName(name);

        if (app.isPresent()) {
            return app.get();
        }

        Apps newApp = Apps.builder()
                .id(null)
                .name(name)
                .build();

        return appsRepository.save(newApp);
    }

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String msg) {
        log.error(msg);
        return new ApiErrorException(httpStatus, msg);
    }
}
