package ru.practicum.ewm.mainservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.events.dto.EventsMapper;
import ru.practicum.ewm.mainservice.events.dto.UpdateEventDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/events")
public class AdminEventsController {

    private final AdminEventService adminEventService;

    @GetMapping
    public ResponseEntity<Object> findEvents(@RequestParam(name = "users", required = false) List<Long> usersList,
                                             @RequestParam(name = "states", required = false) List<String> statesList,
                                             @RequestParam(name = "categories", required = false) List<Long> categoriesList,
                                             @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                             @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                             @Valid @RequestParam(name = "from", defaultValue = "0")
                                                    @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                             @Valid @RequestParam(name = "size", defaultValue = "10")
                                                    @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size) {
        return new ResponseEntity<>(EventsMapper.toFullDto(adminEventService.getFilteredEventList(usersList, statesList, categoriesList,
                rangeStart, rangeEnd, from, size)), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<Object> updateEvent(@Valid @PathVariable("eventId")
                                                     @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                              @Valid @RequestBody UpdateEventDto eventDto) {
        return new ResponseEntity<>(EventsMapper.toFullDto(adminEventService.updateEventDataAndStatus(eventId, eventDto)), HttpStatus.OK);
    }
}
