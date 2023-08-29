package ru.practicum.ewm.mainservice.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mainservice.model.Users;
import ru.practicum.ewm.mainservice.repository.UsersRepository;
import ru.practicum.ewm.mainservice.users.dto.UserDto;
import ru.practicum.ewm.mainservice.users.dto.UsersMapper;
import ru.practicum.statsclient.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsersServiceImpl implements AdminUsersService {

    private final UsersRepository usersRepository;
    private final EntityManager entityManager;

    @Override
    public UserDto createUser(UserDto userDto) {
        Users newUser = UsersMapper.fromDto(userDto);
        return UsersMapper.toDto(usersRepository.save(newUser));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if ((ids == null) || (ids.isEmpty())) {
            Query query = entityManager.createQuery("SELECT u FROM Users u ORDER BY u.id");
            query.setFirstResult(from);
            query.setMaxResults(size);
            return UsersMapper.toDto(query.getResultList());
        }
        return UsersMapper.toDto(usersRepository.findAllById(ids));
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserExists(userId);
        usersRepository.deleteById(userId);
    }

    private void checkUserExists(Long userId) {
        if (!usersRepository.existsById(userId)) {
            throw new ApiErrorException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден в базе данных");
        }
    }
}
