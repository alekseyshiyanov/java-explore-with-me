package ru.practicum.ewm.mainservice.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.mainservice.categories.dto.CategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<Object> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Запрос на создание новой категории с параметрами: name = {}", categoryDto.getName());
        return new ResponseEntity<>(adminCategoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> deleteCategory(@Valid @PathVariable("catId")
                                                 @NotNull(message = "Значение 'catId' не может быть равно null")
                                                 @Positive(message = "Значение 'catId' должно быть положительным числом больше нуля") Long catId) {
        log.info("Запрос на удаление категории с id = {}", catId);
        adminCategoryService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<Object> updateCategory(@Valid @PathVariable("catId")
                                                 @NotNull(message = "Значение 'catId' не может быть равно null")
                                                 @Positive(message = "Значение 'catId' должно быть положительным числом больше нуля") Long catId,
                                                 @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Запрос на обновление категории с параметрами: id ={}, name = {}", catId, categoryDto.getName());
        return new ResponseEntity<>(adminCategoryService.updateCategory(categoryDto, catId), HttpStatus.OK);
    }
}
