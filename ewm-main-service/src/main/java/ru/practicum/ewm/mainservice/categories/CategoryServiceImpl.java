package ru.practicum.ewm.mainservice.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mainservice.categories.dto.CategoryMapper;
import ru.practicum.ewm.mainservice.categories.dto.CategoryDto;
import ru.practicum.ewm.mainservice.model.Categories;
import ru.practicum.ewm.mainservice.repository.CategoryRepository;
import ru.practicum.statsclient.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements AdminCategoryService, PublicCategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityManager entityManager;

    @Override
    public CategoryDto createCategory(CategoryDto inputCategoryDto) {
        Categories newCategory = CategoryMapper.fromDto(inputCategoryDto);
        return CategoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        checkCategoryExists(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto inputCategoryDto, Long categoryId) {
        checkCategoryExists(categoryId);

        Categories newCategory = CategoryMapper.fromDto(inputCategoryDto);
        newCategory.setId(categoryId);
        return CategoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Query query = entityManager.createQuery("SELECT c FROM Categories c ORDER BY c.id");
        query.setFirstResult(from);
        query.setMaxResults(size);
        return CategoryMapper.toDto(query.getResultList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Categories cat = categoryRepository.findById(id).orElseThrow(() -> {
            String errMsg = "Категория с id = " + id + " не найдена в базе данных";
            log.error(errMsg);
            return new ApiErrorException(HttpStatus.NOT_FOUND, errMsg);
        });
        return CategoryMapper.toDto(cat);
    }

    private void checkCategoryExists(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ApiErrorException(HttpStatus.NOT_FOUND, "Категория с id = " + categoryId + " не найдена в базе данных");
        }
    }
}
