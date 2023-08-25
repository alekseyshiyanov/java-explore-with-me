package ru.practicum.ewm.mainservice.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.events.dto.EventRequestStatusUpdateDto;
import ru.practicum.ewm.mainservice.events.dto.NewEventDto;
import ru.practicum.ewm.mainservice.events.dto.UpdateEventDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/users")
public class PrivateEventsController {

    private final PrivateEventsService privateEventsService;

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEvents(@Valid @PathVariable("userId")
                                                   @NotNull(message = "Значение 'userId' не может быть равно null")
                                                   @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                            @Valid @RequestParam(name = "from", defaultValue = "0")
                                                   @PositiveOrZero(message = "Параметр запроса 'from' должен быть больше либо равен 0") Integer from,
                                            @Valid @RequestParam(name = "size", defaultValue = "10")
                                                   @Positive(message = "Параметр запроса 'size' должен быть больше 0") Integer size) {
        log.info("Запрос на получение списка событий, добавленных пользователем с userId = {} с параметрами:, from = {}, size = {}", userId, from, size);
        return new ResponseEntity<>(privateEventsService.getEventsListByUser(userId, from, size), HttpStatus.OK);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> createEvent(@Valid @PathVariable("userId")
                                                     @NotNull(message = "Значение 'userId' не может быть равно null")
                                                     @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                              @Valid @RequestBody NewEventDto eventDto) {
        log.info("Запрос на создание нового события от пользователя с userId = {}", userId);
        return new ResponseEntity<>(privateEventsService.createEvent(userId, eventDto), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEvent(@Valid @PathVariable("userId")
                                                @NotNull(message = "Значение 'userId' не может быть равно null")
                                                @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                           @Valid @PathVariable("eventId")
                                           @NotNull(message = "Значение 'eventId' не может быть равно null")
                                           @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId) {
        log.info("Запрос на получение информации о событии с id = {} добавленном пользователем с id = {}", eventId, userId);
        return new ResponseEntity<>(privateEventsService.getEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> updateEvent(@Valid @PathVariable("userId")
                                                  @NotNull(message = "Значение 'userId' не может быть равно null")
                                                  @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                              @Valid @PathVariable("eventId")
                                                  @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                  @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                              @Valid @RequestBody UpdateEventDto eventDto) {
        log.info("Запрос на изменение информации о событии с id = {} добавленном пользователем с id = {}", eventId, userId);
        return new ResponseEntity<>(privateEventsService.updateEvent(userId, eventId, eventDto), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getUserEventRequest(@Valid @PathVariable("userId")
                                                          @NotNull(message = "Значение 'userId' не может быть равно null")
                                                          @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                      @Valid @PathVariable("eventId")
                                                            @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                            @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId) {
        log.info("Запрос на получение информации о событии с id = {} добавленном пользователем с id = {}", eventId, userId);
        return new ResponseEntity<>(privateEventsService.getEventByUserRequest(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> updateEventRequestStatus(@Valid @PathVariable("userId")
                                                               @NotNull(message = "Значение 'userId' не может быть равно null")
                                                               @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                           @Valid @PathVariable("eventId")
                                                           @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                           @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                           @Valid @RequestBody EventRequestStatusUpdateDto dto) {
        log.info("Запрос на изменение информации о событии с id = {} добавленном пользователем с id = {}", eventId, userId);
        return new ResponseEntity<>(privateEventsService.updateEventRequestStatus(userId, eventId, dto), HttpStatus.OK);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequests(@Valid @PathVariable("userId")
                                                  @NotNull(message = "Значение 'userId' не может быть равно null")
                                                  @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId) {
        log.info("Запрос на получение информации о заявках пользователя с id = {} на участие в чужих событиях", userId);
        return new ResponseEntity<>(privateEventsService.getRequests(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> createRequest(@Valid @PathVariable("userId")
                                                    @NotNull(message = "Значение 'userId' не может быть равно null")
                                                    @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                @Valid @RequestParam(value = "eventId", required = false)
                                                @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId) {
        log.info("Добавление запроса от пользователя с id = {} на участие в событии с id = {}", userId, eventId);
        return new ResponseEntity<>(privateEventsService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequest(@Valid @PathVariable("userId")
                                              @NotNull(message = "Значение 'userId' не может быть равно null")
                                              @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                              @Valid @PathVariable("requestId")
                                              @NotNull(message = "Значение 'requestId' не может быть равно null")
                                              @Positive(message = "Значение 'requestId' должно быть положительным числом больше нуля") Long requestId) {
        log.info("Запрос на отмену участия пользователя с id = {} в событии. Идентификатор запроса на участие requestId = {}", userId, requestId);
        return new ResponseEntity<>(privateEventsService.cancelRequest(userId, requestId), HttpStatus.OK);
    }
}
