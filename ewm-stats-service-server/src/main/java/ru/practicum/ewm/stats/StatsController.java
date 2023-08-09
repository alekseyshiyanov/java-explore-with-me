package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsclient.hits.HitDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> createHitRecord(@Valid @RequestBody HitDto hitDto) {
        log.info("Запрос на создание новой записи");
        var ret = statsService.createHitRecord(hitDto);
        return new ResponseEntity<>(ret, HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(@Valid @RequestParam(value = "start")
                                                  @NotEmpty(message = "Параметр 'start' не может быть пустым или быть равен null") String start,
                                           @Valid @RequestParam(value = "end")
                                                  @NotEmpty(message = "Параметр 'end' не может быть пустым или быть равен null") String end,
                                           @RequestParam(value = "uris", required = false) List<String> uris,
                                           @RequestParam(value = "unique", required = false, defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики");
        var ret = statsService.getStats(start, end, uris, unique);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
