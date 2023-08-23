package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.CompilationArray;

public interface CompilationArrayRepository extends JpaRepository<CompilationArray, Long> {
}
