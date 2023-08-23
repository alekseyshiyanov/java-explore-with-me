package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Categories;

public interface CategoryRepository extends JpaRepository<Categories, Long> {
}
