package ru.practicum.ewm.mainservice.categories;

import ru.practicum.ewm.mainservice.categories.dto.CategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(CategoryDto inputCategoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto updateCategory(CategoryDto inputCategoryDto, Long categoryId);
}
