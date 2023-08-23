package ru.practicum.ewm.mainservice.events;

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
import ru.practicum.ewm.mainservice.events.dto.EventsMapper;
import ru.practicum.ewm.mainservice.statistics.StatisticsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/events")
public class PublicEventsController {

    private final PublicEventService publicEventService;

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<Object> getEventsByQuery( @RequestParam(name = "text", required = false) String searchString,
                                             @RequestParam(name = "categories", required = false) List<Long> categoriesList,
                                             @RequestParam(name = "paid", required = false) Boolean paid,
                                             @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                             @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                             @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                             @RequestParam(name = "sort", required = false) String sort,
                                             @Valid @RequestParam(name = "from", required = false, defaultValue = 0 + "")
                                             @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                             @Valid @RequestParam(name = "size", required = false, defaultValue = 10 + "")
                                             @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size,
                                             HttpServletRequest httpServletRequest) {
        LocalDateTime ldt = LocalDateTime.now();

        var ret = publicEventService.getFilteredEventList(searchString, categoriesList,  paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        statisticsService.sendStatisticData(httpServletRequest.getRemoteAddr(), httpServletRequest.getRequestURI(), ldt);

        return new ResponseEntity<>(EventsMapper.toFullDto(ret), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Object> getEventsById(@Valid @PathVariable("eventId")
                                                    @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                HttpServletRequest httpServletRequest) {
        LocalDateTime ldt = LocalDateTime.now();

        var ret = publicEventService.getPublihedEventsById(eventId);

        statisticsService.sendStatisticData(httpServletRequest.getRemoteAddr(), httpServletRequest.getRequestURI(), ldt);

        return new ResponseEntity<>(EventsMapper.toFullDto(ret), HttpStatus.OK);
    }
}
