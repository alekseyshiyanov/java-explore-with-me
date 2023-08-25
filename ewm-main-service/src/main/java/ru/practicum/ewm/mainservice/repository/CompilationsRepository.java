package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Compilations;

public interface CompilationsRepository extends JpaRepository<Compilations, Long> {
}
