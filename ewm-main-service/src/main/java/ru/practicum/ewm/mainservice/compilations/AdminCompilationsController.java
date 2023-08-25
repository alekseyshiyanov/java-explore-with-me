package ru.practicum.ewm.mainservice.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.mainservice.compilations.dto.UpdateCompilationDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    ResponseEntity<Object> createCompilation(@Valid @RequestBody NewCompilationDto inputDto) {
        return new ResponseEntity<>(adminCompilationService.createCompilation(inputDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    ResponseEntity<Object> deleteCompilation(@Valid @PathVariable("compId")
                                             @PositiveOrZero(message = "Параметр 'compId' должен быть положительным числом")  Long compId) {
        adminCompilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    ResponseEntity<Object> updateCompilation(@Valid @PathVariable("compId")
                                             @PositiveOrZero(message = "Параметр 'compId' должен быть положительным числом")  Long compId,
                                             @Valid @RequestBody UpdateCompilationDto inputDto) {
        var r = adminCompilationService.updateCompilation(compId, inputDto);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }
}
