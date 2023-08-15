package ru.practicum.ewm.mainservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.mainservice.model.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
