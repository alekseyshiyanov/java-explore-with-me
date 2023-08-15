package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Events;

public interface EventsRepository extends JpaRepository<Events, Long> {
}
