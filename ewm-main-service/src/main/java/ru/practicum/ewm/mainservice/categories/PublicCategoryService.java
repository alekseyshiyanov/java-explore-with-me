package ru.practicum.ewm.mainservice.categories;

import ru.practicum.ewm.mainservice.categories.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(Long id);
}
