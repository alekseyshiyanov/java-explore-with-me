package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Requests;

public interface RequestsRepository extends JpaRepository<Requests, Long> {
}
