package ru.practicum.ewm.mainservice.categories.dto;

import ru.practicum.ewm.mainservice.model.Categories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {
    public static List<CategoryDto> toDto(List<Categories> categoriesList) {
        if (categoriesList == null) {
            return Collections.emptyList();
        }

        return categoriesList.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public static CategoryDto toDto(Categories categories) {
        if (categories == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(categories.getId())
                .name(categories.getName())
                .build();
    }

    public static Categories fromDto(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        return Categories.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
