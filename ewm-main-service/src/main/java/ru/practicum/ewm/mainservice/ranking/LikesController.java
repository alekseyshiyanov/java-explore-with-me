package ru.practicum.ewm.mainservice.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/likes")
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/event/{eventId}/users/{userId}")
    public ResponseEntity<Object> evaluateEvent(@Valid @PathVariable("eventId")
                                                @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                @Valid @PathVariable("userId")
                                                @NotNull(message = "Значение 'userId' не может быть равно null")
                                                @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                @Valid @RequestParam(name = "grade", defaultValue = "DISLIKE")
                                                @NotBlank(message = "Параметр запроса 'grade' не должен состоять только из пробелов") String grade) {
        return new ResponseEntity<>(likesService.evaluateEvent(eventId, userId, grade), HttpStatus.OK);
    }

    @DeleteMapping("/event/{eventId}/users/{userId}")
    public ResponseEntity<Object> deleteEventEvaluate(@Valid @PathVariable("eventId")
                                                          @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                          @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                      @Valid @PathVariable("userId")
                                                      @NotNull(message = "Значение 'userId' не может быть равно null")
                                                      @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId) {
        likesService.deleteEventEvaluate(eventId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/event/{eventId}/users/{userId}")
    public ResponseEntity<Object> updateEventEvaluate(@Valid @PathVariable("eventId")
                                                      @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                      @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                      @Valid @PathVariable("userId")
                                                      @NotNull(message = "Значение 'userId' не может быть равно null")
                                                      @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                      @Valid @RequestParam(name = "grade", defaultValue = "DISLIKE")
                                                      @NotBlank(message = "Параметр запроса 'grade' не должен состоять только из пробелов") String grade) {
        return new ResponseEntity<>(likesService.updateEventEvaluate(eventId, userId, grade), HttpStatus.OK);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Object> getEventLikes(@Valid @PathVariable("eventId")
                                                    @NotNull(message = "Значение 'eventId' не может быть равно null")
                                                    @Positive(message = "Значение 'eventId' должно быть положительным числом больше нуля") Long eventId,
                                                @Valid @RequestParam(name = "queryType", defaultValue = "ALL")
                                                @NotBlank(message = "Параметр запроса 'queryType' не должен состоять только из пробелов") String queryType,
                                                @Valid @RequestParam(name = "from", defaultValue = "0")
                                                @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                                @Valid @RequestParam(name = "size", defaultValue = "10")
                                                    @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size) {
        return new ResponseEntity<>(likesService.getEventLikes(eventId, queryType, from, size), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Object> getUserLikes(@Valid @PathVariable("userId")
                                                @NotNull(message = "Значение 'userId' не может быть равно null")
                                                @Positive(message = "Значение 'userId' должно быть положительным числом больше нуля") Long userId,
                                                @Valid @RequestParam(name = "queryType", defaultValue = "ALL")
                                                @NotBlank(message = "Параметр запроса 'queryType' не должен состоять только из пробелов") String queryType,
                                                @Valid @RequestParam(name = "from", defaultValue = "0")
                                                @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                                @Valid @RequestParam(name = "size", defaultValue = "10")
                                                @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size) {
        return new ResponseEntity<>(likesService.getUserLikes(userId, queryType, from, size), HttpStatus.OK);
    }
}
