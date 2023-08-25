package ru.practicum.ewm.mainservice.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.mainservice.ranking.dto.RankingMapper;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/ranking/event")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getEventRanking(@Valid @PathVariable("eventId")
                                                      @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId) {
        return new ResponseEntity<>(rankingService.getEventRanking(eventId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getSortedRanking(@Valid @RequestParam(name = "sort", defaultValue = "NONE")
                                                       @NotBlank(message = "Параметр запроса 'sort' не должен состоять только из пробелов") String sort,
                                                   @Valid @RequestParam(name = "from", defaultValue = "0")
                                                       @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                                   @Valid @RequestParam(name = "size", defaultValue = "10")
                                                       @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size) {
        return new ResponseEntity<>(rankingService.getSortedRanking(sort, from, size), HttpStatus.ACCEPTED);
    }
}
