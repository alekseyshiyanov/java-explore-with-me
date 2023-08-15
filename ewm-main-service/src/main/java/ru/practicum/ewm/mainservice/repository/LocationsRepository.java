package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Locations;

public interface LocationsRepository extends JpaRepository<Locations, Long> {
}
