package ru.practicum.ewm.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.model.Apps;

import java.util.Optional;

public interface AppsRepository extends JpaRepository<Apps, Long> {
    Optional<Apps> getAppsByName(String appName);
}
