package ru.practicum.ewm.mainservice.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/categories")
public class PublicCategoriesController {

    private final PublicCategoryService publicCategoryService;

    @GetMapping
    public ResponseEntity<Object> getCategories(@Valid @RequestParam(name = "from", required = false, defaultValue = 0 + "")
                                                      @PositiveOrZero(message = "Параметр запроса 'from' должен быть больше либо равен 0") Integer from,
                                                      @Valid @RequestParam(name = "size", required = false, defaultValue = 10 + "")
                                                      @Positive(message = "Параметр запроса 'size' должен быть больше 0") Integer size) {
        log.info("Запрос на получение списка категорий с параметрами: from = {}, size = {}", from, size);
        return new ResponseEntity<>(publicCategoryService.getCategories(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<Object> getCategoryById(@Valid @PathVariable("catId")
                                                  @NotNull(message = "Значение 'catId' не может быть равно null")
                                                  @Positive(message = "Значение 'catId' должно быть положительным числом больше нуля") Long catId) {
        log.info("Запрос на удаление категории с id = {}", catId);;
        return new ResponseEntity<>(publicCategoryService.getCategoryById(catId), HttpStatus.OK);
    }
}
