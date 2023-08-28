package ru.practicum.ewm.mainservice.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mainservice.model.*;
import ru.practicum.ewm.mainservice.ranking.dto.LikesDto;
import ru.practicum.ewm.mainservice.ranking.dto.LikesMapper;
import ru.practicum.ewm.mainservice.ranking.dto.RankingDto;
import ru.practicum.ewm.mainservice.ranking.dto.RankingMapper;
import ru.practicum.ewm.mainservice.repository.EventsRepository;
import ru.practicum.ewm.mainservice.repository.LikesRepository;
import ru.practicum.ewm.mainservice.repository.UsersRepository;
import ru.practicum.statsclient.exceptions.ApiErrorException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

/**
 * Формула расчета рейтинга события
 * <br> Средневзвешенный рейтинг = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
 * <br> R = общий рейтинг события (среднее арифметическое всех голосов)
 * <br> v = число голосов за события
 * <br> m = минимум голосов, требуемый для участия в списке (берем равным 5) -> ranking_system_minimum_votes
 * <br> C = среднее значение всего рейтинга (берем равным 0.7) -> ranking_system_rating_average
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RankingServiceImpl implements LikesService, RankingService {

    private final LikesRepository likesRepository;
    private final EventsRepository eventsRepository;
    private final UsersRepository usersRepository;

    private final EntityManager entityManager;

    @Value("${ranking_system.rating_average}")
    private Double averageRating;

    @Value("${ranking_system.minimum_votes}")
    private Long minimumVotes;

    private final String sumQuery = "sum(case l.grade when ?1 then 1 else 0 end)";

    @Override
    public LikesDto evaluateEvent(Long eventId, Long userId, String grade) {
        LikeState state = prepareLikeState(grade);

        var event = getEventById(eventId);
        if (event.getUser().getId().equals(userId)) {
            throw sendErrorMessage(HttpStatus.CONFLICT, "Инициатор не может оценить свое событие");
        }

        var user = getUserById(userId, HttpStatus.NOT_FOUND);

        Likes newLike = Likes.builder()
                .id(null)
                .user(user)
                .event(event)
                .grade(state)
                .build();

        return LikesMapper.toDto(likesRepository.save(newLike));
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesDto> getEventLikes(Long eventId, String queryType, int from, int size) {
        var likeQueryType = prepareLikeQueryType(queryType);
        checkEventExists(eventId, HttpStatus.NOT_FOUND);

        Query query;

        if (likeQueryType == LikeQueryType.ALL) {
            query = entityManager.createQuery("SELECT l FROM Likes l WHERE l.event.id = ?1 ORDER BY l.id");
        } else {
            query = entityManager.createQuery("SELECT l FROM Likes l WHERE l.event.id = ?1 and l.grade = ?2 ORDER BY l.id");

            if (likeQueryType == LikeQueryType.POSITIVE) {
                query.setParameter(2, LikeState.LIKE);
            } else {
                query.setParameter(2, LikeState.DISLIKE);
            }
        }

        query.setParameter(1, eventId);
        query.setFirstResult(from);
        query.setMaxResults(size);

        return LikesMapper.toDto(query.getResultList());
    }

    @Override
    public void deleteEventEvaluate(Long eventId, Long userId) {
        var like = likesRepository.findLikesByEventIdAndUserId(eventId, userId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Оценка с указанными параметрами не найдена в базе данных. " +
                        "Текущие параметры: eventId = " + eventId + " userId = " + userId));
        likesRepository.delete(like);
    }

    @Transactional(readOnly = true)
    @Override
    public List<LikesDto> getUserLikes(Long userId, String queryType, int from, int size) {
        var likeQueryType = prepareLikeQueryType(queryType);
        checkUserExists(userId, HttpStatus.NOT_FOUND);

        Query query;

        if (likeQueryType == LikeQueryType.ALL) {
            query = entityManager.createQuery("SELECT l FROM Likes l WHERE l.user.id = ?1 ORDER BY l.id");
        } else {
            query = entityManager.createQuery("SELECT l FROM Likes l WHERE l.user.id = ?1 and l.grade = ?2 ORDER BY l.id");

            if (likeQueryType == LikeQueryType.POSITIVE) {
                query.setParameter(2, LikeState.LIKE);
            } else {
                query.setParameter(2, LikeState.DISLIKE);
            }
        }

        query.setParameter(1, userId);
        query.setFirstResult(from);
        query.setMaxResults(size);

        return LikesMapper.toDto(query.getResultList());
    }

    @Transactional(readOnly = true)
    @Override
    public RankingDto getEventRanking(Long eventId) {
        checkEventExists(eventId, HttpStatus.NOT_FOUND);

        try {
            String queryString = "select count(l), " + sumQuery + ", (count(l) / (count(l) + " + Double.valueOf(minimumVotes) +
                    ")) * (1.0 * " + sumQuery + " / count(l)) + (" + Double.valueOf(minimumVotes) +
                    " / (count(l) + " + Double.valueOf(minimumVotes) + ")) * " + averageRating + ", e" +
                    " from Likes l, Events e where l.event.id = ?2 and e.id = ?2 group by e.id";

            Object[] ret = (Object[]) entityManager.createQuery(queryString)
                    .setParameter(1, LikeState.LIKE.ordinal())
                    .setParameter(2, eventId)
                    .getSingleResult();

            return RankingMapper.toDto(RankingMapper.fromQuery(ret, minimumVotes));
        } catch (NoResultException e) {
            throw sendErrorMessage(HttpStatus.NOT_FOUND, "Оценки для события с id = " + eventId + " не найдены в базе данных");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<RankingDto> getSortedRanking(String sort, int from, int size) {
        var sortType = prepareRankingSortType(sort);

        try {
            StringBuilder queryString = new StringBuilder("select count(l), " + sumQuery + ", " +
                    "case when count(l) > " + minimumVotes + " then ((count(l) / (count(l) + "
                    + Double.valueOf(minimumVotes) + ")) * (1.0 * " + sumQuery + " / count(l)) + ("
                    + Double.valueOf(minimumVotes) + " / (count(l) + "
                    + Double.valueOf(minimumVotes) + ")) * " + averageRating + ") else 0 end as rating, e" +
                    " from Likes l, Events e where l.event.id = e.id group by e.id");

            switch (sortType) {
                case NONE:
                    queryString.append(" order by e.d asc");
                    break;
                case RATING:
                    queryString.append(" order by rating desc");
                    break;
            }

            List<Object[]> ret = (List<Object[]>) entityManager.createQuery(queryString.toString())
                    .setParameter(1, LikeState.LIKE.ordinal())
                    .setFirstResult(from)
                    .setMaxResults(size)
                    .getResultList();

            return RankingMapper.toDto(RankingMapper.fromQuery(ret, minimumVotes));
        } catch (NoResultException e) {
            throw sendErrorMessage(HttpStatus.NOT_FOUND, "Оценки для событий не найдены в базе данных");
        }
    }

    @Override
    public LikesDto updateEventEvaluate(Long eventId, Long userId, String grade) {
        LikeState state = prepareLikeState(grade);

        var like = likesRepository.findLikesByEventIdAndUserId(eventId, userId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Оценка с указанными параметрами не найдена в базе данных. " +
                "Текущие параметры: eventId = " + eventId + " userId = " + userId));

        like.setGrade(state);

        return LikesMapper.toDto(likesRepository.save(like));
    }

    private LikeState prepareLikeState(String grade) {
        return LikeState.from(grade).orElseThrow(() -> sendErrorMessage(HttpStatus.BAD_REQUEST,
                "Неверное значение параметра 'grade'. Текущее значение: " + grade));
    }

    private RankingSortType prepareRankingSortType(String sort) {
        return RankingSortType.from(sort).orElseThrow(() -> sendErrorMessage(HttpStatus.BAD_REQUEST,
                "Неверное значение параметра 'sort'. Текущее значение: " + sort));
    }

    private LikeQueryType prepareLikeQueryType(String queryType) {
        return LikeQueryType.from(queryType).orElseThrow(() -> sendErrorMessage(HttpStatus.BAD_REQUEST,
                "Неверное значение параметра 'queryType'. Текущее значение: " + queryType));
    }

    private void checkEventExists(Long eventId, HttpStatus httpStatus) {
        if (!eventsRepository.existsById(eventId)) {
            throw sendErrorMessage(httpStatus, "Событие с id = " + eventId + " не найдено в базе данных");
        }
    }

    private Users getUserById(Long userId, HttpStatus httpStatus) {
        return usersRepository.findById(userId).orElseThrow(() ->
                sendErrorMessage(httpStatus, "Пользователь с id = " + userId + " не найден в базе данных"));
    }

    private void checkUserExists(Long userId, HttpStatus httpStatus) {
        if (!usersRepository.existsById(userId)) {
            throw sendErrorMessage(httpStatus, "Пользователь с id = " + userId + " не найден в базе данных");
        }
    }

    private Events getEventById(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                sendErrorMessage(HttpStatus.NOT_FOUND, "Информация о событии с Id = " + eventId
                        + " не найдена в базе данных"));
    }

    private ApiErrorException sendErrorMessage(HttpStatus httpStatus, String errMsg) {
        log.error(errMsg);
        return new ApiErrorException(httpStatus, errMsg);
    }
}
