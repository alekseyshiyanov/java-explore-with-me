package ru.practicum.ewm.mainservice.compilations;

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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/compilations")
public class PublicCompilationsController {

    private final PublicCompilationsService publicCompilationsService;

    @GetMapping
    public ResponseEntity<Object> getCompilationsList(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                      @Valid @RequestParam(name = "from", required = false, defaultValue = 0 + "")
                                                        @PositiveOrZero(message = "Параметр 'from' должен быть положительным числом") int from,
                                                      @Valid @RequestParam(name = "size", required = false, defaultValue = 10 + "")
                                                        @Positive(message = "Параметр 'size' должен быть положительным числом больше 0") int size) {
        return new ResponseEntity<>(publicCompilationsService.getCompilationList(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<Object> getCompilationsById(@Valid @PathVariable("compId")
                                                          @PositiveOrZero(message = "Параметр 'compId' должен быть положительным числом")  Long compId) {
        return new ResponseEntity<>(publicCompilationsService.getCompilation(compId), HttpStatus.OK);
    }
}
